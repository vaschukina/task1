FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY . /app/.
RUN mvn -f /app/pom.xml clean install

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/task3-1.0-SNAPSHOT.jar /app/task3.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/task3.jar"]