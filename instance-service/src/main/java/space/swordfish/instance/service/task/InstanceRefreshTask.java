/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.task;

import com.amazonaws.services.ec2.AmazonEC2Async;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.common.auth.domain.User;
import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.service.EC2Sync;
import space.swordfish.instance.service.service.EC2UserClient;

@Slf4j
@Component
public class InstanceRefreshTask extends TaskBase {

  @Autowired private EC2Sync ec2Sync;

  @Autowired private EC2UserClient ec2UserClient;

  @Autowired private AuthenticationService authenticationService;

  @PostConstruct
  public void scheduleRunnableWithCronTrigger() {
    CronTrigger cronTrigger = new CronTrigger("0 0 0/2 * * ?");

    taskScheduler.schedule(new RunnableTask(), cronTrigger);
  }

  /** Thread for finding new instances on AWS when the cron-trigger stipulates */
  class RunnableTask implements Runnable {

    @Override
    public void run() {
      for (User user : authenticationService.getLocalUsers()) {

        AmazonEC2Async amazonEC2Async = ec2UserClient.amazonEC2Async(user);

        if (amazonEC2Async != null) {
          Iterable<Instance> instances = ec2Sync.syncAll(amazonEC2Async);
          for (Instance instance : instances) {
            notificationService.send(
                "server_refresh", "server_refresh", jsonTransformService.write(instance));
          }
        }
      }
    }
  }
}
