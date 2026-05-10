# Ubuntu 24.04 单机部署 OpenStack 教程

> 更新时间：2026-05-10

## 概述

本文档介绍如何在 Ubuntu 24.04 LTS 系统上使用 DevStack 快速部署 OpenStack 单节点环境。DevStack 是 OpenStack 官方推荐的开发环境部署工具，通过执行脚本自动完成 OpenStack 各组件的安装与配置。

## 环境要求

### 硬件配置

| 项目 | 最低要求 | 推荐配置 |
|------|---------|----------|
| CPU | 4 核 | 8 核 |
| 内存 | 8 GB | 16 GB |
| 磁盘 | 50 GB | 100 GB |
| 网络 | 单网卡 | 千兆网卡 |

### 软件要求

- Ubuntu 24.04 LTS
- root 或 sudo 权限
- 稳定的互联网连接

## 部署方案

### 方案特点

使用 DevStack 部署工具，其特点包括：

- **快速部署**：15-30 分钟即可完成全量安装
- **版本新颖**：基于 stable/2025.2 稳定版本（代号 Gazpacho）
- **管理便捷**：使用 systemd 管理服务
- **扩展性强**：支持插件扩展 OpenStack 服务

### 节点规划

| 主机名 | 管理IP | OpenStack IP | CPU | 内存 | 磁盘 | 操作系统 | 网卡 |
|--------|--------|--------------|-----|------|------|---------|------|
| devstack | 10.126.41.106 | 10.126.41.105 | 8C | 16G | 100G | Ubuntu 24.04 LTS | enp51s0f0（管理）、enp51s0f1（OpenStack） |

### 网络架构图

```
                    ┌─────────────────┐
                    │     Router      │
                    │  10.126.41.1   │
                    │     Gateway     │
                    └────────┬────────┘
                             │
         外部网络: 10.126.41.0/24
                             │
              ┌──────────────┴──────────────┐
              │                             │
              ▼                             ▼
     ┌─────────────┐               ┌─────────────┐
     │ enp51s0f0   │               │ enp51s0f1   │
     │ 10.126.41.106│               │ 10.126.41.105│
     │ (管理网卡)   │               │(OpenStack网卡)│
     └──────┬──────┘               └──────┬──────┘
            │                             │
            └───────────┬─────────────────┘
                        │
               ┌────────▼────────┐
               │   devstack host │
               │                 │
               │  ┌───────────┐  │
               │  │   br-ex   │  │
               │  │ (外部网桥)│  │
               │  └────┬──────┘  │
               │       │         │
               │  ┌────▼────┐    │
               │  │   br-int│    │
               │  │ (内部网桥)│   │
               │  └────┬────┘    │
               │       │         │
               │  ┌────▼────┐    │
               │  │   VM    │    │
               │  │ instances│   │
               │  └─────────┘    │
               └─────────────────┘
```

**网络说明**：
- **外部网络**：10.126.41.0/24
- **网关**：10.126.41.1
- **管理IP（SSH）**：10.126.41.106（enp51s0f0）
- **OpenStack IP**：10.126.41.105（enp51s0f1）
- **浮动IP池**：10.126.41.200 - 10.126.41.210

## 安装步骤

### 第一步：系统基础配置

#### 更新系统软件包

```bash
apt-get update && apt-get upgrade -y
```

#### 设置主机名

```bash
hostnamectl set-hostname devstack
```

#### 配置时区和时间同步

```bash
apt install -y chrony
timedatectl set-timezone Asia/Shanghai
```

#### 配置国内阿里云 APT 源

```bash
cp /etc/apt/sources.list{,.bak}
sed -i 's#http://cn.archive.ubuntu.com/#http://mirrors.aliyun.com/#g' /etc/apt/sources.list
```

### 第二步：网络配置

#### 查看网卡信息

```bash
ip a
```

确认网卡名称为 **enp51s0f0** 和 **enp51s0f1**。

#### 编辑网络配置文件（双网卡配置）

```bash
cat > /etc/netplan/00-installer-config.yaml << EOF
network:
  version: 2
  ethernets:
    # 管理网卡 - 用于 SSH 远程连接
    enp51s0f0:
      dhcp4: false
      addresses:
        - 10.126.41.106/24
      nameservers:
        addresses:
          - 223.5.5.5
          - 223.6.6.6
      routes:
        - to: default
          via: 10.126.41.1

    # OpenStack 网卡 - 用于虚拟机网络和浮动IP
    enp51s0f1:
      dhcp4: false
      addresses:
        - 10.126.41.105/24
EOF
```

#### 应用网络配置

```bash
netplan apply
```

#### 验证网络配置

```bash
ip a show enp51s0f0
ip a show enp51s0f1
```

### 第三步：创建 Stack 用户

DevStack 必须以非 root 用户身份运行，需要创建专用 stack 用户：

```bash
useradd -s /bin/bash -d /opt/stack -m stack
chmod +x /opt/stack
echo "stack ALL=(ALL) NOPASSWD: ALL" | tee /etc/sudoers.d/stack
```

### 第四步：下载 DevStack

切换到 stack 用户并克隆 DevStack 仓库，指定使用 **stable/2025.2** 稳定版本：

```bash
sudo -u stack -i
git clone -b stable/2025.2 https://opendev.org/openstack/devstack
cd devstack
```

> **版本说明**：stable/2025.2 是 OpenStack 2025 年的第二个稳定版本（代号 Gazpacho），包含了最新的功能改进和安全修复。

### 第五步：配置 local.conf

在 devstack 目录下创建 local.conf 配置文件：

```bash
cat > local.conf << EOF
[[local|localrc]]

# Git 仓库源（网络较差时使用国内镜像）
GIT_BASE="https://github.com"

# OpenStack 服务绑定地址（使用 OpenStack 网卡）
HOST_IP=10.126.41.105
DEST=/opt/stack/
LOGDIR=\$DEST/logs
LOGFILE=\$LOGDIR/stack.sh.log

# 认证密码配置
ADMIN_PASSWORD=secret
DATABASE_PASSWORD=\$ADMIN_PASSWORD
RABBIT_PASSWORD=\$ADMIN_PASSWORD
SERVICE_PASSWORD=\$ADMIN_PASSWORD

# 服务主机配置
SERVICE_HOST=10.126.41.105
MYSQL_HOST=\$SERVICE_HOST
RABBIT_HOST=\$SERVICE_HOST
GLANCE_HOSTPORT=\$SERVICE_HOST:9292

# Neutron 网络配置
Q_USE_SECGROUP=True
FLOATING_RANGE="10.126.41.0/24"
Q_FLOATING_ALLOCATION_POOL=start=10.126.41.200,end=10.126.41.210
PUBLIC_NETWORK_GATEWAY="10.126.41.1"
PUBLIC_INTERFACE=enp51s0f1

# Open vSwitch 配置
Q_USE_PROVIDERNET_FOR_PUBLIC=True
OVS_PHYSICAL_BRIDGE=br-ex
PUBLIC_BRIDGE=br-ex
OVS_BRIDGE_MAPPINGS=public:br-ex
EOF
```

**配置参数说明**：

| 参数 | 说明 |
|------|------|
| HOST_IP | OpenStack 服务绑定地址（10.126.41.105） |
| ADMIN_PASSWORD | OpenStack admin 和 demo 用户密码 |
| DATABASE_PASSWORD | MySQL 数据库密码 |
| RABBIT_PASSWORD | RabbitMQ 消息队列密码 |
| FLOATING_RANGE | 浮动 IP 地址池（10.126.41.0/24） |
| PUBLIC_INTERFACE | OpenStack 物理网卡名称（enp51s0f1） |
| PUBLIC_NETWORK_GATEWAY | 外部网络网关（10.126.41.1） |

**双网卡部署说明**：
- **enp51s0f0（管理网卡）**：IP 10.126.41.106，用于 SSH 远程连接和系统管理
- **enp51s0f1（OpenStack 网卡）**：IP 10.126.41.105，用于虚拟机网络和浮动 IP

### 第六步：执行部署

```bash
./stack.sh
```

部署过程需要 15-30 分钟，取决于网络连接速度。安装过程中会自动下载并编译所有组件。

### 第七步：验证部署

#### 查看服务状态

```bash
openstack network list
openstack image list
```

#### 访问 OpenStack Dashboard

打开浏览器访问：

```bash
http://10.126.41.105/dashboard
```

- 用户名：admin 或 demo
- 密码：secret（配置的密码）

### 第八步：配置环境变量

加载 OpenStack CLI 环境变量：

```bash
cd /opt/stack/devstack
source openrc
```

此后即可使用 openstack 命令行工具管理云平台。

## 浮动 IP 配置

### 浮动 IP 池说明

本配置中浮动 IP 范围为 192.168.72.220 - 192.168.72.230，共 11 个可用 IP。

### 使用浮动 IP

1. 登录 Horizon Dashboard
2. 选择 "Project" -> "Network" -> "Floating IPs"
3. 点击 "Allocate IP to Project"
4. 选择外部网络并分配浮动 IP
5. 将浮动 IP 关联到虚拟机实例

## 网络架构说明

### 部署后的网络结构

| 网桥 | 说明 |
|------|------|
| br-ex | 外部网桥，连接物理网卡和外部网络 |
| br-int | 集成网桥，用于内部网络通信 |

### 网络流量走向

```bash
外部网络 <-> ens33 <-> br-ex <-> OpenStack 实例
```

## 常见问题

### 问题一：部署过程中断怎么办

```bash
./stack.sh
```

重新执行即可，DevStack 支持断点续装。

### 问题二：如何卸载 DevStack

```bash
./unstack.sh
```

如需完全清理：

```bash
./clean.sh
```

### 问题三：br-ex 网桥丢失 IP

重启后可能导致 br-ex 网桥 IP 丢失，执行以下命令恢复：

```bash
sudo ip addr flush ens33
sudo systemctl restart devstack@*
```

### 问题四：忘记 admin 密码怎么办

修改 local.conf 中的 ADMIN_PASSWORD，然后重新运行：

```bash
./unstack.sh
./stack.sh
```

## 后续操作

### 创建虚拟机实例

1. 上传镜像（cirros、Ubuntu 等）
2. 创建私有网络
3. 创建路由器并连接网络
4. 启动虚拟机并关联浮动 IP

### 扩展存储

如需添加更多存储节点，可参考官方文档配置 Cinder 多后端存储。

### 高可用配置

生产环境建议配置多节点高可用集群，可参考 DevStack 高可用配置文档。

## 参考资源

- [DevStack 官方文档](https://docs.openstack.org/devstack/latest/)
- [DevStack 项目地址](https://opendev.org/openstack/devstack)
- [OpenStack 官方文档](https://docs.openstack.org/)

---

*祝您部署顺利！*
