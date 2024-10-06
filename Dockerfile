# Stage 1: Build
FROM gradle:jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

# Stage 2: Run
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
