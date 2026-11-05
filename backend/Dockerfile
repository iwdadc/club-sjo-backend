# Build (Imagen con Maven y JDK 21)
FROM maven:3.9-eclipse-temurin-21 AS build 
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# Los tests ya corrieron en el pipeline (job "test")
RUN mvn -B -q package -DskipTests

# Runtime (Imagen con JDK 21 y Alpine)
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar