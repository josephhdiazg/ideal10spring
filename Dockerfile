# syntax=docker/dockerfile:1

FROM node:22-alpine AS frontend-build
WORKDIR /workspace
# Stub stage for a future Vite frontend. When frontend/ exists, copy it here and
# run the Vite production build before copying assets into the Spring image.
RUN mkdir -p /workspace/frontend-dist

FROM eclipse-temurin:21-jdk-alpine AS backend-build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=backend-build /workspace/target/*.jar app.jar
COPY --from=frontend-build /workspace/frontend-dist/ /app/public/
USER spring
EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
