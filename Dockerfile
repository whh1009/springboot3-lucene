FROM maven:3.9-eclipse-temurin-21-alpine AS build
# 配置maven仓库
COPY maven/conf/settings.xml /root/.m2/

WORKDIR /data/app
#
ADD pom.xml .
RUN mkdir -p .m2/repository
RUN mvn install -Dmaven.repo.local=./.m2/repository
# 添加源代码并编译
ADD src ./src
# 编译
RUN mvn -Dmaven.repo.local=./.m2/repository install -Dmaven.test.skip=true

# 构建镜像jdk21
FROM openjdk:21-ea-21-jdk-slim

# 验证 Java 安装
RUN java -version

VOLUME /tmp


EXPOSE 9090

# 运行
ARG JAR_FILE=target/springboot3.jar

# 复制jar包
COPY --from=build /data/app/${JAR_FILE} app.jar


ENTRYPOINT ["java","-jar","/app.jar"]


# docker compose up --build