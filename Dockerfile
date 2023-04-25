#FROM adoptopenjdk:17-jre-hotspot
FROM openjdk:17-alpine
#FROM amazoncorretto:17

VOLUME /tmp

ARG JAR_FILE=build/libs/MusicQ-Service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} musicq-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/musicq-service.jar"]
