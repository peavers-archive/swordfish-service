package space.swordfish.restore.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.restore.service.api.snapshot.SilverstripeSnapshot;
import space.swordfish.restore.service.api.stack.SilverstripeStack;
import space.swordfish.restore.service.domain.Snapshot;
import space.swordfish.restore.service.domain.Stack;
import space.swordfish.restore.service.domain.StackEvent;
import space.swordfish.restore.service.domain.Transfer;

import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
public class SilverstripeServiceImpl implements SilverstripeService {

    @Autowired
    private SilverstripeStack silverstripeStack;

    @Autowired
    private SilverstripeSnapshot silverstripeSnapshot;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<Stack> getAllStacks() {
        return jsonTransformService
                .readList(Stack.class, silverstripeStack.listAll().getBody().toString())
                .get();
    }

    @Override
    public Future<String> createSnapshot(String projectId, StackEvent stackEvent) {

        Callable<String> task = () -> {
            String createResult = silverstripeSnapshot.create(stackEvent.getProjectId(), stackEvent).getBody().toString();

            Transfer transferProgress = getTransferProgress(stackEvent.getProjectId(), createResult);

            while (!transferProgress.getStatus().equals("Finished")) {
                transferProgress = getTransferProgress(stackEvent.getProjectId(), createResult);

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

            ResponseEntity<JsonNode> completeTransferData = silverstripeSnapshot.view(projectId, transferProgress.getSnapshot().getId());

            return setStackIdOnSnapshot(completeTransferData.getBody().toString(), projectId);
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();

        return executor.submit(task);
    }

    private String setStackIdOnSnapshot(String payload, String stackId) {
        Snapshot snapshot = jsonTransformService.read(Snapshot.class, payload);
        snapshot.setStackId(stackId);

        log.info("Complete snapshot {}", snapshot);

        return jsonTransformService.write(snapshot);
    }

    private Transfer getTransferProgress(String projectId, String createResult) {
        Transfer transfer = jsonTransformService.read(Transfer.class, createResult);
        ResponseEntity<JsonNode> transferState = silverstripeSnapshot.transfer(projectId, transfer.getId());

        log.info("Transfer state {}", transfer);

        return jsonTransformService.read(Transfer.class, transferState.getBody().toString());
    }

}
