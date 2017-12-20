FROM java:8-jre
MAINTAINER Chris Turner <peavers@gmail.com>

RUN curl -o /tmp/key.p12 ${CERT_LOCATION}

ADD ./build/libs/edge-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/edge-service-1.0.jar"]

EXPOSE 8443
