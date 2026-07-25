#!/bin/bash
set -e

BACKUP_FILE=${1:-""}
ENV_DIR=${2:-production}

if [ -z "$BACKUP_FILE" ] || [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ 用法: $0 <备份文件路径> [production|staging]"
    echo ""
    echo "可用的备份文件:"
    ls -lh ~/app/backups/*.sql 2>/dev/null || echo "   (无备份文件)"
    exit 1
fi

cd ~/app/$ENV_DIR

echo "⚠️  将从备份恢复数据库: $BACKUP_FILE"
echo "⚠️  当前数据库所有数据将被覆盖！"
read -p "确认继续? (输入 YES 确认): " CONFIRM
if [ "$CONFIRM" != "YES" ]; then
    echo "已取消"
    exit 0
fi

source .env 2>/dev/null || true
DB_PASSWORD=${DB_PASSWORD:-root}

echo "⏳ 恢复中..."
docker compose exec -T mysql sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD" personal_blog' < "$BACKUP_FILE"

echo "✅ 数据库恢复完成！"
echo "建议重启应用: docker compose restart app"