# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (for layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S tracko && adduser -S tracko -G tracko

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/uploads && chown -R tracko:tracko /app

USER tracko

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
