package space.swordfish.restore.service.listener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.restore.service.domain.StackEvent;
import space.swordfish.restore.service.service.JsonTransformService;
import space.swordfish.restore.service.service.QueueMessageService;
import space.swordfish.restore.service.service.SilverstripeService;

@Slf4j
@Service
@EnableSqs
public class QueueListener {

	private SilverstripeService silverstripeService;

	private JsonTransformService jsonTransformService;

	private QueueMessageService queueMessageService;

	@Autowired
	public QueueListener(SilverstripeService silverstripeService,
			JsonTransformService jsonTransformService,
			QueueMessageService queueMessageService) {
		this.silverstripeService = silverstripeService;
		this.jsonTransformService = jsonTransformService;
		this.queueMessageService = queueMessageService;
	}

	/**
	 * Reads events from InstanceQueue and determines what to do with them.
	 *
	 * @param payload String representation of a InstanceEvent
	 */
	@MessageMapping(value = "${queues.restoreEvents}")
	public void instanceCommandHandler(String payload) {
		StackEvent stackEvent = jsonTransformService.read(StackEvent.class, payload);
		Future<String> snapshot = silverstripeService
				.createSnapshot(stackEvent.getProjectId(), stackEvent);

		while (!snapshot.isDone()) {
			try {
				String link = snapshot.get(60, TimeUnit.SECONDS);
				queueMessageService
						.send("SnapshotRestoreQueue-" + stackEvent.getInstanceId(), link);
				log.info("snapshot ready to download from: {}", link);
			}
			catch (InterruptedException | ExecutionException | TimeoutException e) {
				log.warn("timeout error {}", e.getLocalizedMessage());
			}
		}
	}
}
