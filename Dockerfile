FROM adoptopenjdk:11-jdk-hotspot

WORKDIR /app

COPY ./target/service-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "service-0.0.1-SNAPSHOT.jar"]