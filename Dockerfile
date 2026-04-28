# Stage 1: build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
RUN ./gradlew --version
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN ./gradlew dependencies --no-daemon -q || true
COPY src/ src/
RUN ./gradlew shadowJar --no-daemon -x test

# Stage 2: run
FROM eclipse-temurin:21-jre AS runner
WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
