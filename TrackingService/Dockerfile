FROM maven:3.6.3-openjdk-11-slim AS maven
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn package
FROM adoptopenjdk:11-jre
WORKDIR /usr/app
# COPY ./config/settings.properties ./config/settings.properties
# ENV TRACKING_SERVICE_SETTINGS_LOCATION=./config/settings.properties
COPY --from=maven target/TrackingService-*-jar-with-dependencies.jar ./TrackingService.jar
ENTRYPOINT ["java", "-jar", "./TrackingService.jar"]