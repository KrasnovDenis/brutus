FROM openjdk:17
MAINTAINER Denis Krasnov
LABEL APPLICATION=BRUTUS
FROM maven:3.6-openjdk-17 AS build
                                                                                                                                                                                                                COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

COPY --from=build /usr/src/app/target/Brutus-0.0.1-SNAPSHOT.jar /usr/app/Brutus-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/Brutus-0.0.1-SNAPSHOT.jar"]