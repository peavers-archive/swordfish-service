[![Build Status](https://travis-ci.org/peavers/swordfish-service.svg?branch=master)](https://travis-ci.org/peavers/swordfish-service)
[![Maintainability](https://api.codeclimate.com/v1/badges/e188e6e8833420cba06a/maintainability)](https://codeclimate.com/github/peavers/swordfish-service/maintainability)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/peavers/swordfish/blob/master/LICENSE)

# Swordfish 

**Simplify creating developer machines on AWS.**

Makes use of Spring Cloud and Emberjs to create a simple way for developers to create/start/stop/terminate preset AMI's on AWS.  

#### Creating Certificates for edge-service
HTTPS should be used when talking ot the edge service. This is super easy thanks to LetsEncrypt

```
docker create \
  --cap-add=NET_ADMIN \
  --name=letsencrypt \
  -v ~/letsencrypt:/config \
  -e PGID=1000 -e PUID=1000  \
  -e EMAIL= \
  -e ONLY_SUBDOMAINS= \
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
