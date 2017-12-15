package space.swordfish.instance.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;
import space.swordfish.instance.service.service.InstanceEC2Service;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InstanceShutdownTask {

	private ThreadPoolTaskScheduler taskScheduler;

	private CronTrigger cronTrigger;

	private InstanceRepository instanceRepository;

	private InstanceEC2Service instanceEC2Service;

	@Autowired
	public InstanceShutdownTask(
			@Qualifier("threadPoolTaskScheduler") ThreadPoolTaskScheduler taskScheduler,
			CronTrigger cronTrigger, InstanceRepository instanceRepository,
			InstanceEC2Service instanceEC2Service) {
		this.taskScheduler = taskScheduler;
		this.cronTrigger = cronTrigger;
		this.instanceRepository = instanceRepository;
		this.instanceEC2Service = instanceEC2Service;
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
					.findAllByStateAndProduction("running", false);

			for (Instance instance : instances) {
				log.info("automatically shutting down {}", instance.getTags());

				instanceEC2Service.stop(instance.getInstanceId());
			}
		}
	}
}
