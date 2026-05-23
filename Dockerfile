FROM eclipse-temurin:21-jdk AS build

LABEL maintainer="FIAP"
LABEL description="Aplicacao Java Spring Boot ClyvoVet com Gradle"

ARG APP_HOME=/app

WORKDIR ${APP_HOME}

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN chmod +x gradlew
RUN ./gradlew clean build -x test


FROM eclipse-temurin:21-jre

LABEL app="clyvovet-api"
LABEL version="1.0"

ARG APP_HOME=/app

WORKDIR ${APP_HOME}

ENV APP_PORT=8080
ENV JAVA_OPTS=""

RUN useradd -m appuser

COPY --from=build /app/build/libs/*.jar app.jar

ADD README.md /app/README.md

VOLUME ["/app/logs"]

EXPOSE 8080

USER appuser

CMD [""]

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]