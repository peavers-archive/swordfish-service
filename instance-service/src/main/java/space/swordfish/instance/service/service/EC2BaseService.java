package space.swordfish.instance.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import space.swordfish.common.auth.services.Auth0Service;
import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

import java.util.UUID;

abstract class EC2BaseService {
    @Autowired
    Auth0Service auth0Service;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    InstanceRepository instanceRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    JsonTransformService jsonTransformService;

    @Autowired
    EC2KeyPair ec2KeyPair;

    @Autowired
    EC2Sync ec2Sync;

    String createUniqueId(String seed) {
        return UUID.nameUUIDFromBytes(seed.getBytes()).toString();
    }

    void refreshClientInstance(Instance instance) {
        notificationService.send("server_refresh", "server_refresh", jsonTransformService.write(ec2Sync.getByInstance(instance)));
    }
}
