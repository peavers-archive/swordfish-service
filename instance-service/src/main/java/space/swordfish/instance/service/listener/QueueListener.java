package space.swordfish.instance.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.instance.service.domain.Instance;

@Service
@EnableSqs
public class QueueListener {

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private RestTemplate restTemplate;

    @MessageMapping("${queues.instanceEvents}")
    public void instanceCommandHandler(String payload) {

        // Transform the payload to the object so we can get the preset JWT
        Instance instance = jsonTransformService.read(Instance.class, payload);

        // Load the JWT into the internal request header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + instance.getUserToken());
        HttpEntity<String> instanceEntity = new HttpEntity<>(payload, headers);

        // Decide and set where to fire the request to
        String endpoint;
        switch (instance.getSwordfishCommand()) {
            case "start": {
                endpoint = "/start";
                break;
            }
            case "stop": {
                endpoint = "/stop";
                break;
            }
            case "create": {
                endpoint = "/create";
                break;
            }
            case "reboot": {
                endpoint = "/reboot";
                break;
            }
            case "terminate": {
                endpoint = "/terminate";
                break;
            }
            default: {
                endpoint = "/error";
            }
        }

        // Fire the initial string payload through to the correct controller endpoint
        restTemplate.postForObject("http://instance-service/instances" + endpoint, instanceEntity, String.class);
    }
}
