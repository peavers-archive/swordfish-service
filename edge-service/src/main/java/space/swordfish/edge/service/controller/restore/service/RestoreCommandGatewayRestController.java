package space.swordfish.edge.service.controller.restore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.edge.service.domain.StackEvent;

import java.io.IOException;

@Slf4j
@RestController
public class RestoreCommandGatewayRestController {

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${queues.restoreEvents}")
    private String queue;

    @PostMapping("/stack-events")
    public ResponseEntity<String> event(@RequestBody String payload) {
        try {
            String result = java.net.URLDecoder.decode(payload, "UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();

            StackEvent stackEvent = objectMapper.readValue(result, StackEvent.class);
            stackEvent.setUserToken(authenticationService.getCurrentToken());

            payload = jsonTransformService.write(stackEvent);

            this.queueMessagingTemplate.send(queue,
                    MessageBuilder.withPayload(payload).build());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}