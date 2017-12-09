FROM java:8-jre
MAINTAINER Chris Turner <peavers@gmail.com>

ADD ./build/libs/restore-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/restore-service-1.0.jar"]
