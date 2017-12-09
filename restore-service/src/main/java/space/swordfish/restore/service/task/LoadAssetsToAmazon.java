package space.swordfish.restore.service.task;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.restore.service.domain.Stack;
import space.swordfish.restore.service.repository.StackRepository;
import space.swordfish.restore.service.service.SilverstripeService;

@Slf4j
@Component
public class LoadAssetsToAmazon {

	private final StackRepository stackRepository;
	private final SilverstripeService silverstripeService;
	private ThreadPoolTaskScheduler taskScheduler;
	private CronTrigger cronTrigger;

	@Autowired
	public LoadAssetsToAmazon(ThreadPoolTaskScheduler taskScheduler,
			CronTrigger cronTrigger, StackRepository stackRepository,
			SilverstripeService silverstripeService) {
		this.taskScheduler = taskScheduler;
		this.cronTrigger = cronTrigger;

		this.stackRepository = stackRepository;
		this.silverstripeService = silverstripeService;
	}

	@PostConstruct
	public void scheduleRunnableWithCronTrigger() {
		taskScheduler.schedule(new RunnableTask(), cronTrigger);
	}

	class RunnableTask implements Runnable {

		@Override
		public void run() {
			List<Stack> stacks = silverstripeService.getAllStacks();
			stackRepository.save(stacks);
		}
	}
}
