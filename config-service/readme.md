# Config Server

#### Program arguments
These must be passed into the application
```
--spring.cloud.config.server.git.uri=https://peavers@bitbucket.org/papa_c/config-repo.git
--spring.cloud.config.server.git.username=forloop-readyonly
--spring.cloud.config.server.git.password=Squad1234
```

Or if using Docker
```
SPRING_CLOUD_CONFIG_SERVER_GIT_URI=
SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME=
SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD=
```