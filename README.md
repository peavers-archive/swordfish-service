[![Build Status](https://travis-ci.org/peavers/swordfish.svg?branch=master)](https://travis-ci.org/peavers/swordfish)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/peavers/swordfish/blob/master/LICENSE)

# Swordfish 

**Simplify creating developer machines on AWS.**

Makes use of Spring Cloud and Emberjs to create a simple way for developers to create/start/stop/terminate preset AMI's on AWS.  

**Warning:** This is a very, very early development build...

## Services
A list of the servers in alphabetical order and the basic tasks they preform

### Config Service
Simple Spring Cloud config service, responsible for distributing configuration files to all other services. 

### Edge Service
Zuul gateway where all requests from clients (browsers etc) come into. The Edge Service is responsible for distributing
those requests to all other services. I've implemented this in a CQRS manner using AWS SQS. POST/PATCH/DELETE commands are
sent through to a queue where the relevant services are listening and take action. GET requests are routeed straight through
to their relevant service.  

### Eureka Service
Eureka looks after discovery for all services. This allows us to simply ask for `http://instance-service` in the gateway and 
one will be returned. We don't need to know where in the cluster, or even what port it's running on. 

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