Ubuntu 24.04 单机部署OpenStack教程概述

概述

本文档介绍如何在Ubuntu24.04 LTS Desktop 系统上使用DevStack 快速部署OpenStack

技术选型的思考

官方提供了多种部署方案：

例如DevStack 脚本自动化部署，目的是简化开发

例如Kolla-ansible docker容器化部署

这里为什么采用前者，笔者有两点思考：

1.DevStack入门容易，新手友好，Kolla-ansible部署方式需要学习容器和kolla-ansible相关技术，上手成本高

2.Ubuntu24.04 LTS不支持docker desktop的安装，因为我只会用docker desktop来可视化的管理容器，如果缺乏可视化界面，我会觉得再使用容器化部署的方式就没啥性价比，因为我就是为了容器的可视化管理才会考虑docker部署的

ps:对了，操作系统为什么采用桌面版，是因为涉及到文件的查看和修改，采用ubuntu自带的gnome桌面会方便一点

部署架构图如下：



部署教程

1.升级操作系统版本到Ubuntu24.04 lts

cat /etc/os-release # 查看Ubuntu代号/版本

do-release-upgrade #大版本升级

2.系统更新 && 配置主机名 && 配置时间同步 && 配置国内阿里源

apt-get update && apt-get upgrade -y  # root用户下运行 

hostnamectl set-hostname devstack # 将主机名设置为devstack

apt install -y chrony 
timedatectl set-timezone Asia/Shanghai #将时区配置到上海

3.配置国内阿里APT源

cp /etc/apt/sources.list{,.bak}
sed -i 's#http://cn.archive.ubuntu.com/#http://mirrors.aliyun.com/#g' /etc/apt/sources.list



4.添加 Stack 用户

创建一个单独的stack用户来运行 DevStack

sudo useradd -s /bin/bash -d /opt/stack -m stack

添加权限

sudo chmod +x /opt/stack #确保 `stack` 用户的主目录对所有人都具有可执行权限

!(C:\Users\20847\AppData\Roaming\Typora\typora-user-images\image-20260510202621842.png)

由于该用户将对您的系统进行许多更改，因此它应该具有 sudo 权限：

```bash
echo "stack ALL=(ALL) NOPASSWD: ALL" | sudo tee /etc/sudoers.d/stack
sudo -u stack -i
```

5.配置国内pip源，需要切换到stack用户

mkdir ~/.pip
cat > ~/.pip/pip.conf << EOF 
[global]
trusted-host=mirrors.aliyun.com
index-url=https://mirrors.aliyun.com/pypi/simple/
EOF

6.下载Devstack

```elixir
git clone https://opendev.org/openstack/devstack
```

```elixir
cd devstack
```
