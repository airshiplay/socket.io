FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY maven/socket.io-webssh-1.0.0-SNAPSHOT-exec.jar app.jar
RUN mkdir -p dependency && cd dependency && jar -xf ../app.jar

RUN cp -r /dependency/BOOT-INF/classes/ /app
RUN cp -r /dependency/BOOT-INF/lib/ /app/lib
RUN cp -r /dependency/META-INF/ /app/META-INF
EXPOSE 8080

ENTRYPOINT ["java","-cp","/app:/app/lib/*","${start-class}"]

