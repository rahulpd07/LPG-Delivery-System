FROM --platform=linux/amd64 openjdk:23-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable jar file from the target directory into the container
COPY build/libs/lpg_delivery-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that the application will listen on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]