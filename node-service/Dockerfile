FROM java:8-jre
MAINTAINER Chris Turner <peavers@gmail.com>

ADD ./build/libs/node-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/node-service-1.0.jar"]

EXPOSE 9090