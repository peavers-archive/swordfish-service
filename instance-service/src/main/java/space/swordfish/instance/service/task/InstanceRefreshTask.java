package space.swordfish.instance.service.task;

import com.amazonaws.services.ec2.AmazonEC2Async;
import com.auth0.json.mgmt.users.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.service.EC2Sync;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InstanceRefreshTask extends TaskBase {

    @Autowired
    private EC2Sync ec2Sync;

    @PostConstruct
    public void scheduleRunnableWithCronTrigger() {
        CronTrigger cronTrigger = new CronTrigger("0 0 0/2 * * ?");

        taskScheduler.schedule(new RunnableTask(), cronTrigger);
    }

    /**
     * Thread for finding new instances on AWS when the cron-trigger stipulates
     */
    class RunnableTask implements Runnable {

        @Override
        public void run() {
            Iterable<User> allUsers = auth0Service.getAllUsers();
            for (User user : allUsers) {

                AmazonEC2Async amazonEC2Async = customAmazonEC2Async(user);

                if (amazonEC2Async != null) {
                    Iterable<Instance> instances = ec2Sync.syncAll(amazonEC2Async);
                    for (Instance instance : instances) {
                        notificationService.send("server_refresh", "server_refresh", jsonTransformService.write(instance));
                    }
                }
            }
        }
    }
}
