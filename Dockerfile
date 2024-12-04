FROM openjdk:17-jdk-slim as builder
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/dynamicpricing-0.0.1-SNAPSHOT.jar /app/app.jar

COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

EXPOSE 8080
ENTRYPOINT ["/app/wait-for-it.sh", "mongo:27017", "--", "java", "-jar", "/app/app.jar", "--debug"]
