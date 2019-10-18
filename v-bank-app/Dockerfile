FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/v-bank-app-1.0-SNAPSHOT.war
COPY ${JAR_FILE} /v-bank-app.war
ENTRYPOINT ["java","-jar","/v-bank-app.war"]