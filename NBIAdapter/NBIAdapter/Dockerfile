# Use maven to compile the java application.
FROM docker.io/maven AS build-env

# Set the working directory to /app
WORKDIR /app

# copy the pom.xml file to download dependencies
COPY pom.xml ./
ADD https://millwall.cofe.btireland.ie/tools/public-config/raw/master/maven/settings.xml /root/.m2/

# download dependencies as specified in pom.xml
# building dependency layer early will speed up compile time when pom is unchanged
RUN mvn dependency:go-offline -Drevision=1.0

# Copy the rest of the working directory contents into the container
COPY . ./

# Compile the application.
RUN mvn package -Dmaven.test.skip=true -Drevision=1.0

# Build runtime image.
FROM openjdk:11-jre-slim

# Copy the compiled files over.
COPY --from=build-env /app/target/ /app/

# Starts java app with debugging server at port 5005.
CMD ["java", "-jar", "/app/springboot-microservice-1.0.jar"]
