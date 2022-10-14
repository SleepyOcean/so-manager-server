FROM openjdk:8u322
COPY so-manager-admin.jar /ocean/so-manager-server.jar
EXPOSE 8080
CMD ["java", "-jar", "-XX:MaxRAM=1024m", "/ocean/so-manager-server.jar"]