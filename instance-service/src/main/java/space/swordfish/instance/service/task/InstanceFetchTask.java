package space.swordfish.instance.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.instance.service.service.InstanceEC2Service;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InstanceFetchTask {

	private ThreadPoolTaskScheduler taskScheduler;

	private InstanceEC2Service instanceEC2Service;

	@Autowired
	public InstanceFetchTask(
			@Qualifier("threadPoolTaskScheduler") ThreadPoolTaskScheduler taskScheduler,
			InstanceEC2Service instanceEC2Service) {
		this.taskScheduler = taskScheduler;
		this.instanceEC2Service = instanceEC2Service;
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
			instanceEC2Service.syncAll();
		}
	}
}
