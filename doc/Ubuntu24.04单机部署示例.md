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
                    ┌──────────────────────────┐
                    │         Router           │
                    │  ┌───────────────────┐   │
                    │  │   WAN 口(公网)    │   │
                    │  │    IP: xxx        │   │
                    │  └────────┬──────────┘   │
                    │           │               │
                    │  ┌────────▼──────────┐   │
                    │  │   LAN 口(内网)    │   │
                    │  │  10.126.40.1/21   │   │
                    │  └────────┬──────────┘   │
                    └───────────┼──────────────┘
                               │
                   内网网段: 10.126.40.0/21
                               │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
     ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
     │ enp51s0f0   │   │ enp51s0f1   │   │   其他主机   │
     │ 10.126.41.106│   │ 10.126.41.105│   │  10.126.x.x │
     │ (管理网卡)   │   │(OpenStack网卡)│   │             │
     └──────┬──────┘   └──────┬──────┘   └─────────────┘
            │                 │
            └────────┬────────┘
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
- **内网网段**：10.126.40.0/21（子网掩码 21 位，共 2046 个可用IP）
- **网关内网IP**：10.126.40.1
- **网关公网IP**：xxx（WAN口，用于访问外网）
- **管理IP（SSH）**：10.126.41.106（enp51s0f0）
- **OpenStack IP**：10.126.41.105（enp51s0f1）
- **浮动IP池**：10.126.43.200 - 10.126.43.210

**路由转发规则**：
| 目标网络 | 转发路径 | 是否走公网 |
|----------|----------|-----------|
| 10.126.40.0/21 | 网关LAN口直接转发 | ❌ 不走公网 |
| 其他网络 | 网关WAN口转发 | ✅ 走公网 |

**说明**：同网段（10.126.40.0/21）内的主机通信直接通过内网网关转发，不经过公网；访问外网或跨网段时才通过网关的公网口转发。

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
        - 10.126.41.106/21
      nameservers:
        addresses:
          - 223.5.5.5
          - 223.6.6.6
      routes:
        - to: default
          via: 10.126.40.1

    # OpenStack 网卡 - 用于虚拟机网络和浮动IP
    enp51s0f1:
      dhcp4: false
      addresses:
        - 10.126.41.105/21
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

### 第四步：配置国内阿里云 PIP 源

切换到 stack 用户并配置 PIP 源，加速 Python 包下载：

```bash
sudo -u stack -i
mkdir ~/.pip
cat > ~/.pip/pip.conf << EOF
[global]
trusted-host=mirrors.aliyun.com
index-url=https://mirrors.aliyun.com/pypi/simple/
EOF
```

### 第五步：下载 DevStack

切换到 stack 用户并克隆 DevStack 仓库，指定使用 **stable/2025.2** 稳定版本：

```bash
git clone -b stable/2025.2 https://opendev.org/openstack/devstack
cd devstack
```

> **版本说明**：stable/2025.2 是 OpenStack 2025 年的第二个稳定版本（代号 Gazpacho），包含了最新的功能改进和安全修复。

### 第六步：配置 local.conf

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
ADMIN_PASSWORD=suma123456
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
FLOATING_RANGE="10.126.40.0/21"
Q_FLOATING_ALLOCATION_POOL=start=10.126.43.200,end=10.126.43.210
PUBLIC_NETWORK_GATEWAY="10.126.40.1"
PUBLIC_INTERFACE=enp51s0f1

# Open vSwitch provider networking 配置
Q_USE_PROVIDERNET_FOR_PUBLIC=True
OVS_PHYSICAL_BRIDGE=br-ex
PUBLIC_PHYSICAL_NETWORK=public
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
| FLOATING_RANGE | 浮动 IP 地址池（10.126.40.0/21） |
| Q_FLOATING_ALLOCATION_POOL | 浮动 IP 分配范围（10.126.43.200-10.126.43.210） |
| PUBLIC_NETWORK_GATEWAY | 外部网络网关（10.126.40.1） |
| PUBLIC_INTERFACE | OpenStack 物理网卡名称（enp51s0f1） |

**双网卡部署说明**：
- **enp51s0f0（管理网卡）**：IP 10.126.41.106，用于 SSH 远程连接和系统管理
- **enp51s0f1（OpenStack 网卡）**：IP 10.126.41.105，用于虚拟机网络和浮动 IP

### 第七步：执行部署

DevStack 部署有两种方式，可根据实际情况选择：

#### 方式一：直接部署（适合本地终端或稳定连接）

如果您在本地终端操作，或网络连接非常稳定，可以直接执行部署脚本：

```bash
cd ~/devstack
./stack.sh
```

**优点**：
- 操作简单，无需额外工具
- 实时查看部署日志输出

**缺点**：
- SSH 连接中断会导致部署失败
- 关闭终端会中断部署进程

#### 方式二：使用 screen 会话部署（推荐用于远程 SSH）

如果通过 SSH 远程连接部署，强烈建议使用 screen 会话，避免网络中断导致部署失败：

```bash
# 1. 切到 devstack 目录
cd ~/devstack

# 2. 新建 screen 会话
screen -S devstack

# 3. 在 screen 里直接跑部署
./stack.sh
```

**Screen 会话操作说明**：
- 按 `Ctrl+A` 然后按 `D`：退出 screen 会话（部署继续在后台运行）
- 使用 `screen -r devstack`：重新连接到会话查看部署进度
- 使用 `screen -ls`：查看当前所有 screen 会话
- 使用 `Ctrl+A` 然后按 `K`：关闭当前 screen 会话

**优点**：
- SSH 连接中断不影响部署进程
- 可以随时断开/重新连接查看进度
- 部署在后台持续运行

#### 两种方式对比

| 对比项 | 直接部署 | screen 部署 |
|--------|----------|-------------|
| 适用场景 | 本地终端、稳定连接 | 远程 SSH、不稳定网络 |
| 连接中断影响 | 部署失败 | 无影响，继续后台运行 |
| 操作复杂度 | 简单 | 稍复杂，需学习基本操作 |
| 日志查看 | 实时查看 | 可随时重新连接查看 |

**选择建议**：
- ✅ **使用 screen**：通过 SSH 远程部署、网络不稳定、部署时间较长
- ✅ **直接部署**：本地物理机操作、有图形界面终端、网络极其稳定

部署过程需要 15-30 分钟，取决于网络连接速度。安装过程中会自动下载并编译所有组件。

### 第八步：验证部署

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
- 密码：suma123456（配置的密码）

### 第九步：配置环境变量

加载 OpenStack CLI 环境变量：

```bash
cd /opt/stack/devstack
source openrc
```

此后即可使用 openstack 命令行工具管理云平台。

## 浮动 IP 配置

### 浮动 IP 池说明

本配置中浮动 IP 范围为 10.126.43.200 - 10.126.43.210，共 11 个可用 IP。

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
外部网络 <-> enp51s0f1 <-> br-ex <-> OpenStack 实例
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
sudo ip addr flush enp51s0f1
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