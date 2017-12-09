FROM java:8-jre
MAINTAINER Chris Turner <peavers@gmail.com>

ADD ./build/libs/instance-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/instance-service-1.0.jar"]

