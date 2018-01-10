package space.swordfish.edge.service.controller.user.service;

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
import space.swordfish.edge.service.domain.User;

@Slf4j
@RestController
public class UserCommandGatewayRestController {

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${queues.userEvents}")
    private String queue;

    @PostMapping("/users")
    public ResponseEntity<String> post(@RequestBody String payload) {
        User user = jsonTransformService.read(User.class, payload);
        user.setRequestToken(authenticationService.getCurrentAuth0Token());

        this.queueMessagingTemplate.send(queue, MessageBuilder.withPayload(jsonTransformService.write(user)).build());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This is just here to keep the EmberJS client happy. Just forwards the payload to the standard post method.
     *
     * @param payload String representing a User object
     * @param id      String representing the ID of that user
     * @return ResponseEntity OK
     */
    @PatchMapping("/users/{id}")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> patch(@RequestBody String payload, @PathVariable("id") String id) {
        post(payload);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}