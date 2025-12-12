#!/bin/sh
set -e

# 1️⃣ 从当前工作目录读取 .env（如果有的话）
# 容器运行时，可以把宿主机的 .env 挂载到 /app/.env
if [ -f ".env" ]; then
  echo "[entrypoint] Loading environment from .env"
  # 过滤注释和空行，然后 export
  export $(grep -v '^#' .env | grep -v '^[[:space:]]*$' | xargs) || true
fi

# 2️⃣ 处理 Spring Boot 的外部 yml 配置
# 方案 A：如果挂载了 /config 目录，就自动把它当作额外配置来源
CONFIG_ARGS=""
if [ -d "/config" ]; then
  # 比如在宿主机挂载： -v $(pwd)/config:/config
  CONFIG_ARGS="--spring.config.additional-location=file:/config/"
fi

# 方案 B：如果你想指定单独的配置文件，也可以运行容器时传入：
#   -e SPRING_CONFIG_FILE=file:/config/application-prod.yml
# 然后这里帮你拼起来：
if [ -n "$SPRING_CONFIG_FILE" ]; then
  CONFIG_ARGS="--spring.config.location=${SPRING_CONFIG_FILE}"
fi

echo "[entrypoint] Starting app on port 8080..."

# 3️⃣ 启动 Spring Boot
exec java $JAVA_OPTS -jar app.jar $CONFIG_ARGS
