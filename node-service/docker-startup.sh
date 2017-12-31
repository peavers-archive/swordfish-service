#!/usr/bin/env bash

echo "Set InstanceId for message queue"
AWS_CHANNEL=$(curl http://169.254.169.254/latest/meta-data/instance-id) \

echo "Launching jar"
java -Xmx200m -jar /app/node-service-1.0.jar