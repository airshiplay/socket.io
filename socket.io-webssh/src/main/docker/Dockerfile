FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8080

COPY maven/socket.io-webssh-1.0.0-exec.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]

#RUN mkdir -p dependency && cd dependency && jar -xf ../app.jar
#
#RUN cp -r /dependency/BOOT-INF/classes/ /app
#RUN cp -r /dependency/BOOT-INF/lib/ /app/lib
#RUN cp -r /dependency/META-INF/ /app/META-INF
#
#RUN rm -rf /dependency /app.jar
#
#ENTRYPOINT ["java","-cp","/app:/app/lib/*","${start-class}"]

