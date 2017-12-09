FROM java:8-jre
MAINTAINER Chris Turner <peavers@gmail.com>

ADD ./build/libs/notification-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/notification-service-1.0.jar"]