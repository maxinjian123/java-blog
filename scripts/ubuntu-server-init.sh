#!/bin/bash
set -e

echo "=============================================="
echo "  Ubuntu 服务器初始化脚本 (Docker + 环境准备)"
echo "=============================================="

if [ "$EUID" -ne 0 ]; then
    echo "请使用 sudo 运行此脚本: sudo $0"
    exit 1
fi

echo ""
echo "[1/6] 更新系统软件包..."
export DEBIAN_FRONTEND=noninteractive
apt update -y && apt upgrade -y
apt install -y apt-transport-https ca-certificates curl gnupg lsb-release ufw

echo ""
echo "[2/6] 安装 Docker 官方仓库..."
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://mirrors.aliyun.com/docker-ce/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

echo ""
echo "[3/6] 安装 Docker Engine + Docker Compose..."
apt update -y
apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

echo ""
echo "[4/6] 配置 Docker 镜像加速与日志滚动..."
mkdir -p /etc/docker
cat > /etc/docker/daemon.json << 'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com",
    "https://mirror.ccs.tencentyun.com"
  ],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m",
    "max-file": "3"
  },
  "storage-driver": "overlay2"
}
EOF
systemctl daemon-reload
systemctl enable docker --now
usermod -aG docker "$SUDO_USER" || true

echo ""
echo "[5/6] 配置防火墙 (仅开放必要端口)..."
ufw default deny incoming
ufw default allow outgoing
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 8080/tcp
echo "y" | ufw enable || true
ufw status verbose || true

echo ""
echo "[6/6] 创建部署目录与权限..."
mkdir -p /home/$SUDO_USER/app/{staging,production,backups}
mkdir -p /home/$SUDO_USER/app/production/sql
chown -R $SUDO_USER:$SUDO_USER /home/$SUDO_USER/app

echo ""
echo "=============================================="
echo "✅ 初始化完成！"
echo "=============================================="
echo ""
echo "Docker 版本: $(docker --version)"
echo "Compose 版本: $(docker compose version 2>&1 || echo '未安装')"
echo ""
echo "⚠️  请执行以下命令使 docker 组生效 (或重新登录 SSH):"
echo "   newgrp docker"
echo ""
echo "📌 下一步配置 GitHub Secrets:"
echo "   1. 生成 SSH Key: ssh-keygen -t ed25519 -C 'github-ci' -N '' -f ~/.ssh/github_ci"
echo "   2. 将公钥添加到 authorized_keys:"
echo "      cat ~/.ssh/github_ci.pub >> ~/.ssh/authorized_keys"
echo "      chmod 600 ~/.ssh/authorized_keys"
echo "   3. 私钥内容复制到 GitHub Secrets (PROD_SSH_KEY / STAGING_SSH_KEY):"
echo "      cat ~/.ssh/github_ci"
echo "      （复制完整内容，包括 -----BEGIN OPENSSH PRIVATE KEY----- 开头的全部内容）"
echo ""