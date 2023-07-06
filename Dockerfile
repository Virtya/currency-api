FROM adoptopenjdk:11-jdk-hotspot as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apt-get update && \
    apt-get install -y maven && \
    mvn package -DskipTests

FROM adoptopenjdk:11-jre-hotspot

WORKDIR /app

COPY --from=builder /app/target/service-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "service-0.0.1-SNAPSHOT.jar"]