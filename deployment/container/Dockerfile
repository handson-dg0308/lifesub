FROM openjdk:21-jdk-slim

ARG BUILD_LIB_DIR
ARG ARTIFACTORY_FILE

WORKDIR /app

COPY ${BUILD_LIB_DIR}/${ARTIFACTORY_FILE} /app/app.jar

ENV PORT=8080
EXPOSE ${PORT}

CMD ["java", "-jar", "app.jar"]
