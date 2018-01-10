FROM java:8-jre
MAINTAINER Chris Turner <peavers@gmail.com>

ADD ./build/libs/user-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/user-service-1.0.jar"]