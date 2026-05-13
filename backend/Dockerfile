# 多阶段构建 - 后端
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 复制依赖文件
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建项目
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 从 .env 文件复制环境变量
COPY .env /.env

# 复制 JAR 文件
COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090

# 使用 .env 文件中的变量
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
