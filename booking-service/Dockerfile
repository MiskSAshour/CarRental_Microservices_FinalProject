FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/booking-service-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

EXPOSE 8081
