FROM openjdk:17
MAINTAINER Denis Krasnov
LABEL APPLICATION=BRUTUS

COPY target/Brutus-0.0.2-SNAPSHOT.jar /usr/app/Brutus-0.0.2-SNAPSHOT.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/usr/app/Brutus-0.0.2-SNAPSHOT.jar"]