# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn ./.mvn
RUN ./mvnw -q -B -T 1C dependency:go-offline
COPY src ./src
RUN ./mvnw -q -B -T 1C clean package -DskipTests && \
    java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# ---------- Runtime stage ----------
# layers copied dependency-first for optimal Docker layer caching
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=build /app/target/extracted/dependencies/ ./
COPY --from=build /app/target/extracted/spring-boot-loader/ ./
COPY --from=build /app/target/extracted/snapshot-dependencies/ ./
COPY --from=build /app/target/extracted/application/ ./
USER spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget -qO- http://localhost:8080/actuator/health | grep -q UP
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-XX:+ExitOnOutOfMemoryError","-Djava.security.egd=file:/dev/./urandom","org.springframework.boot.loader.launch.JarLauncher"]