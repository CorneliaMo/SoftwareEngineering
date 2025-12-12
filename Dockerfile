# =========================
# 1️⃣ 构建阶段：用 JDK + Gradle 构建 jar
# =========================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# 先拷贝 Gradle 包装器和配置，加快缓存命中
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle ./
COPY build.gradle ./
# 如果有 gradle.properties 也一起加上（没有就删掉这一行）
# COPY gradle.properties ./

# 确保 gradlew 可执行
RUN chmod +x gradlew

# 先预下载依赖（利用 Docker 层缓存）
RUN ./gradlew dependencies --no-daemon || true

# 再拷贝源码
COPY src ./src

# 构建 Spring Boot jar（你也可以用 build，看你项目习惯）
RUN ./gradlew clean bootJar -x test --no-daemon

# =========================
# 2️⃣ 运行阶段：用 JRE 轻量运行
# =========================
FROM eclipse-temurin:17-jre-alpine AS runtime

# 给 Spring Boot 一个固定工作目录
WORKDIR /app

# 从构建阶段拷贝打包好的 jar
# 注意：如果你的 jar 名字固定，可以改成具体名字，比如 my-app.jar
COPY --from=build /app/build/libs/*.jar /app/app.jar

# 拷贝启动脚本，用来读取 .env + 处理外部 yml
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# 暴露 Spring Boot 默认端口
EXPOSE 8080

# 预留 JVM 参数、Spring 额外配置目录
ENV JAVA_OPTS=""
ENV SPRING_CONFIG_ADDITIONAL_LOCATION="file:/config/"

# 用脚本作为入口
ENTRYPOINT ["/app/entrypoint.sh"]
