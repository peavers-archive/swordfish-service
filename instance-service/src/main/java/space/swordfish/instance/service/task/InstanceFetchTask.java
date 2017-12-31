package space.swordfish.instance.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;
import space.swordfish.instance.service.service.EC2Sync;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InstanceFetchTask {

    @Autowired
    private EC2Sync ec2Sync;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private InstanceRepository instanceRepository;

    @PostConstruct
    public void scheduleRunnableWithCronTrigger() {
        CronTrigger cronTrigger = new CronTrigger("0 0/5 * * * ?");

        taskScheduler.schedule(new RunnableTask(), cronTrigger);
    }

    /**
     * Thread for finding new instances on AWS when the cron-trigger stipulates
     */
    class RunnableTask implements Runnable {

        @Override
        public void run() {
            Iterable<Instance> instances = instanceRepository.findAll();

            for (Instance instance : instances) {
                ec2Sync.sync(instance);
            }
        }
    }
}
