#FROM adoptopenjdk:17-jre-hotspot
FROM openjdk:17-alpine
#FROM amazoncorretto:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} test-sample.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/test-sample.jar"]
