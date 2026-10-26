FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/clubbies-api-0.1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
