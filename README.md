[![Build Status](https://travis-ci.org/peavers/swordfish-service.svg?branch=master)](https://travis-ci.org/peavers/swordfish-service)
[![Maintainability](https://api.codeclimate.com/v1/badges/e188e6e8833420cba06a/maintainability)](https://codeclimate.com/github/peavers/swordfish-service/maintainability)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/peavers/swordfish/blob/master/LICENSE)

# Swordfish 

**Simplify creating developer machines on AWS.**

Makes use of Spring Cloud and Emberjs to create a simple way for developers to create/start/stop/terminate preset AMI's on AWS.  

**Warning:** This is a very, very early development build...

## Services
A list of the servers in alphabetical order and the basic tasks they preform

### Edge Service
Zuul gateway where all requests from clients (browsers etc) come into. The Edge Service is responsible for distributing
those requests to all other services. I've implemented this in a CQRS manner using AWS SQS. POST/PATCH/DELETE commands are
sent through to a queue where the relevant services are listening and take action. GET requests are routeed straight through
to their relevant service.  

#### Creating Certificates for edge-service
HTTPS should be used when talking ot the edge service. This is super easy thanks to LetsEncrypt

```
docker create \
  --cap-add=NET_ADMIN \
  --name=letsencrypt \
  -v ~/letsencrypt:/config \
  -e PGID=1000 -e PUID=1000  \
  -e EMAIL= \
  -e ONLY_SUBDOMAINS=true \
  -e URL= \
  -e SUBDOMAINS= \
  -p 443:443 \
  -e TZ=Pacific/Auckland \
  linuxserver/letsencrypt
```

That will give you a nice clean cert to convert using 
```
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out swordfish-key.p12 -name tomcat
```

### Instance Service
Handles the bulk of the work on AWS when it comes to CRUD operations of EC2 instances.  

### Node Service
Runs on each developers machine, is responsible for the download and restore of databases/assets when requested.

### Notification Service
Each service can send information to a notification queue via SQS. It's this services task to read that queue and decide
what to do with the information, either send it via web sockets to the frontend to update the browser client, or
send a user an email, post to slack etc.

### Restore Service 
Interfaces with Silverstripes Dash application to retrieve a list of stacks that belong to you, along with making calls
to create new snapshots. 

## Docker
Each service is built in Dockerhub and can be ran (assuming consul is setup) with the following
```
docker run -d --name=notification-service \
 -e CONSUL_HTTP_ADDR=$CONSUL_HTTP_ADDR \
 -e SPRING_PROFILES_ACTIVE=docker \
 -e SPRING_CLOUD_CONSUL_HOST=$CONSUL_HTTP_ADDR \
 -e SPRING_CLOUD_CONSUL_PORT=8500 \
 --dns 169.254.1.1 -P peavers/notification-service
```