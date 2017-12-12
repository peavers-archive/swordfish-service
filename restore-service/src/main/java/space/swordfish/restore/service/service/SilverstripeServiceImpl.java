package space.swordfish.restore.service.service;

import java.util.List;
import java.util.concurrent.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.restore.service.api.snapshot.SilverstripeSnapshot;
import space.swordfish.restore.service.api.stack.SilverstripeStack;
import space.swordfish.restore.service.domain.Stack;
import space.swordfish.restore.service.domain.StackEvent;
import space.swordfish.restore.service.domain.Transfer;

@Slf4j
@Service
public class SilverstripeServiceImpl implements SilverstripeService {

	private final SilverstripeStack silverstripeStack;
	private final SilverstripeSnapshot silverstripeSnapshot;
	private final JsonTransformService jsonTransformService;
	private final NotificationService notificationService;

	@Autowired
	public SilverstripeServiceImpl(SilverstripeStack silverstripeStack,
			SilverstripeSnapshot silverstripeSnapshot,
			JsonTransformService jsonTransformService,
			NotificationService notificationService) {
		this.silverstripeStack = silverstripeStack;
		this.silverstripeSnapshot = silverstripeSnapshot;
		this.jsonTransformService = jsonTransformService;
		this.notificationService = notificationService;
	}

	@Override
	public List<Stack> getAllStacks() {
		return jsonTransformService
				.readList(Stack.class, silverstripeStack.listAll().getBody().toString())
				.get();
	}

	@Override
	public Future<String> createSnapshot(String projectId, StackEvent stackEvent) {

		Callable<String> task = () -> {
			Transfer transfer = jsonTransformService.read(Transfer.class,
					silverstripeSnapshot.create(stackEvent.getProjectId(), stackEvent)
							.getBody().toString());
			Transfer transferProgress = transferProgress(
					silverstripeSnapshot.transfer(projectId, transfer.getId()));

			while (!transferProgress.getStatus().equals("Finished")) {

				transferProgress = transferProgress(
						silverstripeSnapshot.transfer(projectId, transfer.getId()));
				log.info("transfer state {}", transferProgress);
				notificationService.send("restore_event", "restore_info",
						"Snapshot building for " + projectId);
				TimeUnit.SECONDS.sleep(5);

				if (transferProgress.getStatus().equals("Failed")) {
					log.warn("Failed snapshot");
					break;
				}
			}

			notificationService.send("restore_event", "restore_success",
					"Snapshot created for " + projectId);
			return transferProgress.getSnapshot().getLinks().getLink("self").getHref();
		};

		ExecutorService executor = Executors.newSingleThreadExecutor();

		return executor.submit(task);
	}

	@Override
	public void getSnapshotById(String id) {

	}

	private Transfer transferProgress(ResponseEntity<JsonNode> transferState) {
		return jsonTransformService.read(Transfer.class,
				transferState.getBody().toString());
	}

}
