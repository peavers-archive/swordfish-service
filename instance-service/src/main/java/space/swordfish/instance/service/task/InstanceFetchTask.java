package space.swordfish.instance.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.instance.service.service.EC2Sync;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InstanceFetchTask {

	private final EC2Sync ec2Sync;
	private final ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	public InstanceFetchTask(EC2Sync ec2Sync,
			@Qualifier("threadPoolTaskScheduler") ThreadPoolTaskScheduler taskScheduler) {
		this.ec2Sync = ec2Sync;
		this.taskScheduler = taskScheduler;
	}

	@PostConstruct
	public void scheduleRunnableWithCronTrigger() {
		CronTrigger cronTrigger = new CronTrigger("0 0/30 * * * ?");

		taskScheduler.schedule(new RunnableTask(), cronTrigger);
	}

	/**
	 * Thread for finding new instances on AWS when the cron-trigger stipulates
	 */
	class RunnableTask implements Runnable {

		@Override
		public void run() {

        }
	}
}
