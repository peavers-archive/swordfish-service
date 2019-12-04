/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.instance.service.repository.InstanceRepository;

abstract class TaskBase {

  @Autowired ThreadPoolTaskScheduler taskScheduler;

  @Autowired NotificationService notificationService;

  @Autowired JsonTransformService jsonTransformService;

  @Autowired InstanceRepository instanceRepository;
}
