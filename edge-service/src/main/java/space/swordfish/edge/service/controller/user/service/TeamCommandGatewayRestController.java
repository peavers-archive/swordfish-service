package space.swordfish.edge.service.controller.user.service;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
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
import space.swordfish.edge.service.domain.Team;

@Slf4j
@RestController
public class TeamCommandGatewayRestController {

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${queues.teamEvents}")
    private String queue;

    @PostMapping("/teams")
    public ResponseEntity<String> post(@RequestBody String payload) {
        log.info(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(payload)));

        Team team = jsonTransformService.read(Team.class, payload);
        team.setRequestToken(authenticationService.getCurrentToken());

        this.queueMessagingTemplate.send(queue, MessageBuilder.withPayload(jsonTransformService.write(team)).build());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This is just here to keep the EmberJS client happy. Just forwards the payload to the standard post method.
     *
     * @param payload String representing a User object
     * @param id      String representing the ID of that user
     * @return ResponseEntity OK
     */
    @PatchMapping("/teams/{id}")
    public ResponseEntity<String> patch(@RequestBody String payload, @PathVariable("id") String id) {
        post(payload);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This is just here to keep the EmberJS client happy. Just forwards the payload to the standard post method.
     *
     * @param id String representing the ID of that user
     * @return ResponseEntity OK
     */
    @DeleteMapping("/teams/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        Team team = new Team();
        team.setId(id);
        team.setSwordfishCommand("delete");

        post(jsonTransformService.write(team));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}