# Step 1: Build the Spring Boot backend
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . . 
# Grant execute permission to mvnw
RUN chmod +x ./mvnw
# Run the Maven build
RUN ./mvnw package -DskipTests

# Step 2: Prepare the runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install Graphviz in runtime 
RUN apt-get update && apt-get install -y graphviz
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
