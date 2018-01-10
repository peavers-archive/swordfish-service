package space.swordfish.edge.service.controller.instance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.edge.service.domain.Instance;

@Slf4j
@RestController
public class InstanceCommandGatewayRestController {

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${queues.instanceEvents}")
    private String queue;

    @PostMapping("/instances")
    public ResponseEntity<String> post(@RequestBody String payload) {
        Instance instance = jsonTransformService.read(Instance.class, payload);
        instance.setUserToken(authenticationService.getCurrentAuth0Token());
        String write = jsonTransformService.write(instance);

        this.queueMessagingTemplate.send(queue, MessageBuilder.withPayload(write).build());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/instances/{id}")
    @DeleteMapping("/instances/{id}")
    public ResponseEntity<String> patch(@RequestBody String payload, @PathVariable("id") String id) {
        post(payload);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
