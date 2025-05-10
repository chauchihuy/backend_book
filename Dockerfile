# Build stage
FROM maven:3.8.6-amazoncorretto-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM amazoncorretto:17-alpine3.17
COPY --from=build /target/shopapp-0.0.1-SNAPSHOT.jar shopapp.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","shopapp.jar"]