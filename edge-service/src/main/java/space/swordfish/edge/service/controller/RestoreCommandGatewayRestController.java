package space.swordfish.edge.service.controller;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import space.swordfish.edge.service.domain.StackEvent;
import space.swordfish.edge.service.service.JsonTransformService;

import java.io.IOException;

@Api(tags = "Restore Command")
@Slf4j
@RestController
public class RestoreCommandGatewayRestController {

    @Value("${queues.restoreEvents}")
    private String queue;

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final JsonTransformService jsonTransformService;

    @Autowired
    public RestoreCommandGatewayRestController(AmazonSQSAsync amazonSqs, QueueMessagingTemplate queueMessagingTemplate, JsonTransformService jsonTransformService) {
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.jsonTransformService = jsonTransformService;

        amazonSqs.createQueueAsync(queue);
    }

    @ApiOperation(value = "Issue a command against a restore point.")
    @PostMapping("/stack-events")
    public ResponseEntity<String> event(@RequestBody String payload) {
        try {
            String result = java.net.URLDecoder.decode(payload, "UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            StackEvent stackEvent = objectMapper.readValue(result, StackEvent.class);

            payload = jsonTransformService.write(stackEvent);

            this.queueMessagingTemplate.send(queue, MessageBuilder.withPayload(payload).build());
        } catch (DocumentSerializationException | IOException e) {
            log.error(e.getLocalizedMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}