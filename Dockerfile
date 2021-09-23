FROM adoptopenjdk/openjdk11:alpine-jre

ARG VERSION

# Refer to Maven build -> finalName
ARG JAR_FILE=target/azure-pipeline-${VERSION}.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
