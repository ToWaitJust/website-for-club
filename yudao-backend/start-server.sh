#!/bin/bash
# 后端启动脚本:读取同目录下 .env 注入环境变量,再启动 yudao-server.jar
# 用法: cd yudao-backend && bash start-server.sh
# 建议配合 nohup / systemd 后台运行

set -e

DIR="$(cd "$(dirname "$0")" && pwd)"
ENV_FILE="$DIR/.env"

# 检查 .env
if [ ! -f "$ENV_FILE" ]; then
    echo "[错误] 未找到 $ENV_FILE ,请先复制 .env.example 为 .env 并填入真实值"
    exit 1
fi

# 逐行读取 .env,跳过注释和空行,export 到环境变量
while IFS='=' read -r key value || [ -n "$key" ]; do
    key="$(echo "$key" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//;s/\r$//')"
    [ -z "$key" ] && continue
    [[ "$key" == \#* ]] && continue
    value="$(echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//;s/\r$//')"
    # 去掉可能包住值的引号
    value="${value%\"}"
    value="${value#\"}"
    value="${value%\'}"
    value="${value#\'}"
    export "$key=$value"
done < "$ENV_FILE"

echo "[信息] 已加载 .env 连接配置"

# 定位 jar (优先同目录下的 yudao-server.jar,其次 target 目录)
JAR="$DIR/yudao-server.jar"
if [ ! -f "$JAR" ]; then
    JAR="$DIR/yudao-server/target/yudao-server.jar"
fi
if [ ! -f "$JAR" ]; then
    echo "[错误] 未找到 yudao-server.jar ,请先执行 mvn package"
    exit 1
fi

echo "[启动] java -jar $(basename "$JAR") --server.port=48080"
exec java -jar "$JAR" --server.port=48080