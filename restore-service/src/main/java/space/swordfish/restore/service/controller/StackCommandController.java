package space.swordfish.restore.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.queue.services.QueueMessageService;
import space.swordfish.restore.service.domain.StackEvent;
import space.swordfish.restore.service.service.SilverstripeService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/stacks")
public class StackCommandController {

    @Autowired
    private SilverstripeService silverstripeService;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private QueueMessageService queueMessageService;

    @PostMapping("/create")
    public void create(@RequestBody String payload) {
        StackEvent stackEvent = jsonTransformService.read(StackEvent.class, payload);

        Future<String> snapshot = silverstripeService.createSnapshot(stackEvent.getProjectId(), stackEvent);

        while (!snapshot.isDone()) {
            try {
                queueMessageService.send(stackEvent.getInstanceId(), snapshot.get(3000, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.warn("timeout error {}", e.getLocalizedMessage());
            }
        }
    }


}
