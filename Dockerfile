FROM openjdk:17
MAINTAINER Denis Krasnov
LABEL APPLICATION=BRUTUS
FROM maven:3.6-openjdk-17 AS build

COPY target/Brutus-0.0.1-SNAPSHOT.jar /usr/app/Brutus-0.0.1-SNAPSHOT.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/usr/app/Brutus-0.0.1-SNAPSHOT.jar"]