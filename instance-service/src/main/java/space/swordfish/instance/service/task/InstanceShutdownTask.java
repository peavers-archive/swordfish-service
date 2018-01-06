package space.swordfish.instance.service.task;

import com.amazonaws.services.ec2.AmazonEC2Async;
import com.auth0.json.mgmt.users.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.service.EC2Stop;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InstanceShutdownTask extends TaskBase {

    @Autowired
    private EC2Stop ec2Stop;

    @PostConstruct
    public void scheduleRunnableWithCronTrigger() {
        CronTrigger cronTrigger = new CronTrigger("00 00 18 * * ?");

        taskScheduler.schedule(new InstanceShutdownTask.RunnableTask(), cronTrigger);
    }

    /**
     * Thread for shutting down servers when the cron-trigger stipulates
     */
    class RunnableTask implements Runnable {

        @Override
        public void run() {
            Iterable<Instance> instances = instanceRepository.findAllByStateAndSwordfishIsTrueAndProductionIsFalse("running");

            for (Instance instance : instances) {
                User user = auth0Service.getUser(instance.getUserId());
                AmazonEC2Async amazonEC2Async = customAmazonEC2Async(user);

                ec2Stop.process(amazonEC2Async, instance);
            }

        }
    }
}
