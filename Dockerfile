FROM openjdk:11
WORKDIR /
COPY target/default+uberjar/crud-*-SNAPSHOT-standalone.jar crud.jar
EXPOSE 3004
ENTRYPOINT ["java", "-jar", "crud.jar"]
