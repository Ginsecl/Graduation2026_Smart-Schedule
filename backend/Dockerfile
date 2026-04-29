FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY pom.xml settings.xml ./
COPY src ./src

RUN mvn clean package -DskipTests -B -s settings.xml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/smart-schedule-backend-1.0.0.jar"]
