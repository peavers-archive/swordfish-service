#!/usr/bin/env bash

echo "Downloading key..."
curl -o /tmp/key.p12 ${CERT_LOCATION}

echo "Launching jar"
java -Xmx200m -jar /app/edge-service-1.0.jar