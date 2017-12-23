package space.swordfish.instance.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;
import space.swordfish.instance.service.service.EC2Stop;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InstanceShutdownTask {

    private final EC2Stop ec2Stop;
    private final CronTrigger cronTrigger;
    private final InstanceRepository instanceRepository;
    private final ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    public InstanceShutdownTask(
            @Qualifier("threadPoolTaskScheduler") ThreadPoolTaskScheduler taskScheduler,
            CronTrigger cronTrigger, InstanceRepository instanceRepository,
            EC2Stop ec2Stop) {
        this.taskScheduler = taskScheduler;
        this.cronTrigger = cronTrigger;
        this.instanceRepository = instanceRepository;
        this.ec2Stop = ec2Stop;
    }

    @PostConstruct
    public void scheduleRunnableWithCronTrigger() {
        taskScheduler.schedule(new RunnableTask(), cronTrigger);
    }

    /**
     * Thread for shutting down servers when the cron-trigger stipulates
     */
    class RunnableTask implements Runnable {

        @Override
        public void run() {
            Iterable<Instance> instances = instanceRepository
                    .findAllByStateAndSwordfishIsTrueAndProductionIsFalse("running");

            for (Instance instance : instances) {
                log.info("automatically shutting down {}", instance.getTags());

                ec2Stop.stop(instance.getInstanceId());
            }
        }
    }
}
