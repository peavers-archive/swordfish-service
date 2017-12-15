package space.swordfish.instance.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InstancePurgeTask {

	private ThreadPoolTaskScheduler taskScheduler;

	private InstanceRepository instanceRepository;

	@Autowired
	public InstancePurgeTask(
			@Qualifier("threadPoolTaskScheduler") ThreadPoolTaskScheduler taskScheduler,
			InstanceRepository instanceRepository) {
		this.taskScheduler = taskScheduler;
		this.instanceRepository = instanceRepository;
	}

	@PostConstruct
	public void scheduleRunnableWithCronTrigger() {
		CronTrigger cronTrigger = new CronTrigger("0 0/30 * * * ?");

		taskScheduler.schedule(new RunnableTask(), cronTrigger);
	}

	/**
	 * Thread for removing terminated instances from storage when the cron-trigger
	 * stipulates
	 */
	class RunnableTask implements Runnable {

		@Override
		public void run() {
			removeTerminated();
		}

		private void removeTerminated() {
			Iterable<Instance> instances = instanceRepository
					.findAllByState("terminated");

			instanceRepository.delete(instances);
		}
	}
}
