FROM maven:3.6.3-openjdk-11-slim AS maven
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn package
FROM adoptopenjdk:11-jre
WORKDIR /usr/app
COPY --from=maven target/Cli-*-jar-with-dependencies.jar ./Cli.jar
ENTRYPOINT ["java", "-jar", "./Cli.jar"]