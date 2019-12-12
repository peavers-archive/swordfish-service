/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

abstract class EC2BaseService {

  @Autowired AuthenticationService authenticationService;

  @Autowired InstanceRepository instanceRepository;

  @Autowired NotificationService notificationService;

  @Autowired JsonTransformService jsonTransformService;

  @Autowired EC2KeyPair ec2KeyPair;

  @Autowired EC2Sync ec2Sync;

  @Autowired EC2Waiter ec2Waiter;

  String createUniqueId(String seed) {
    return UUID.nameUUIDFromBytes(seed.getBytes()).toString();
  }

  void refreshClient(Instance instance) {
    notificationService.send(
        "server_refresh", "server_refresh", jsonTransformService.write(instance));
  }
}
