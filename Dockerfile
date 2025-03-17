FROM openjdk:21-slim

WORKDIR /usr/src/app

COPY target/BachelorGateway.jar /usr/src/app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "BachelorGateway.jar"]