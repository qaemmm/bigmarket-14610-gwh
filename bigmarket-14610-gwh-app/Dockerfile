# 基础镜像
FROM eclipse-temurin:17-jre-jammy

# 作者
LABEL MAINTAINER="842123094"

# 配置
ENV PARAMS=""
ENV TZ=Asia/Shanghai

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ADD target/bigmarket-14610-gwh-app.jar /bigmarket-14610-gwh-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /bigmarket-14610-gwh-app.jar $PARAMS"]