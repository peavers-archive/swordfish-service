package space.swordfish.restore.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.restore.service.domain.StackEvent;

@Slf4j
@Service
@EnableSqs
public class QueueListener {

    private final static String SERVICE = "http://restore-service";

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private RestTemplate restTemplate;

    @MessageMapping("${queues.restoreEvents}")
    public void restoreCommandHandler(String payload) {
        StackEvent stackEvent = jsonTransformService.read(StackEvent.class, payload);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + stackEvent.getUserToken());
        HttpEntity<String> stackEntity = new HttpEntity<>(payload, headers);

        restTemplate.exchange(SERVICE + "/stacks/create", HttpMethod.POST, stackEntity, String.class);
    }
}
