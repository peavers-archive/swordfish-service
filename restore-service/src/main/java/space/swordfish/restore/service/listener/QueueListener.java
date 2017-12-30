package space.swordfish.restore.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.queue.services.QueueMessageService;
import space.swordfish.restore.service.domain.StackEvent;
import space.swordfish.restore.service.service.SilverstripeService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@EnableSqs
public class QueueListener {

    private final SilverstripeService silverstripeService;

    private final JsonTransformService jsonTransformService;

    private final QueueMessageService queueMessageService;

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
    @MessageMapping("${queues.restoreEvents}")
    public void instanceCommandHandler(String payload) {
        StackEvent stackEvent = jsonTransformService.read(StackEvent.class, payload);
        Future<String> snapshot = silverstripeService
                .createSnapshot(stackEvent.getProjectId(), stackEvent);

        log.info("StackEvent {}", stackEvent);

        while (!snapshot.isDone()) {
            try {
                String message = snapshot.get(3000, TimeUnit.SECONDS);

                log.info("Message {}", message);

                queueMessageService.send(stackEvent.getInstanceId(), message);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.warn("timeout error {}", e.getLocalizedMessage());
            }
        }
    }
}
