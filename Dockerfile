FROM adoptopenjdk/openjdk8:latest

RUN apt-get update && \
    apt-get install -y curl

EXPOSE 8080

WORKDIR /home/oereb

ARG JAR_FILE
COPY ${JAR_FILE} /home/oereb/app.jar
RUN chown -R 1001:0 /home/oereb && \
    chmod -R g=u /home/oereb

USER 1001

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/oereb/app.jar"]

HEALTHCHECK --interval=30s --timeout=30s --start-period=60s CMD curl http://localhost:8080/actuator/health