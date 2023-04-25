#FROM adoptopenjdk:17-jre-hotspot
FROM openjdk:17-alpine
#FROM amazoncorretto:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} musicq-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/musicq-service.jar"]
