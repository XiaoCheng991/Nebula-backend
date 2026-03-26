# 多阶段构建：构建阶段
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY pom.xml .
COPY nebula-common/pom.xml nebula-common/
COPY nebula-model/pom.xml nebula-model/
COPY nebula-config/pom.xml nebula-config/
COPY nebula-service/pom.xml nebula-service/
COPY nebula-api/pom.xml nebula-api/
COPY nebula-admin/pom.xml nebula-admin/

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY nebula-common/src nebula-common/src
COPY nebula-model/src nebula-model/src
COPY nebula-config/src nebula-config/src
COPY nebula-service/src nebula-service/src
COPY nebula-api/src nebula-api/src
COPY nebula-admin/src nebula-admin/src

# 构建项目
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:21-jre

WORKDIR /app

# 创建非 root 用户（安全最佳实践）
RUN useradd -m appuser

# 复制构建产物
COPY --from=build /app/nebula-admin/target/*.jar app.jar

# 更改文件所有权
RUN chown appuser:appuser app.jar

# 切换到非 root 用户
USER appuser

EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
