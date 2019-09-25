FROM adoptopenjdk/openjdk8:latest

EXPOSE 8080

WORKDIR /home/oereb

ARG JAR_FILE
COPY ${JAR_FILE} /home/oereb/app.jar
RUN chown -R 1001:0 /home/oereb && \
    chmod -R g=u /home/oereb

USER 1001

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/oereb/app.jar"]
