#FROM --platform=linux/amd64 openjdk:23-jdk-slim
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the executable jar file from the target directory into the container
#COPY build/libs/lpg_delivery-0.0.1-SNAPSHOT.jar /app/app.jar
#
## Expose the port that the application will listen on
#EXPOSE 8080
#
## Run the jar file
#ENTRYPOINT ["java","-jar","app.jar"]

# Stage 1: Build the JAR
FROM gradle:8.5-jdk17 AS build
COPY . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build --no-daemon

# Stage 2: Run the JAR
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/lpg_delivery-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
