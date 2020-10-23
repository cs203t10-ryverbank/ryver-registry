FROM openjdk:11-jre
WORKDIR /ryver-registry
COPY . /app
ENTRYPOINT ["java","-jar","/app/ryver-registry/target/registry-0.0.1-SNAPSHOT.jar"]