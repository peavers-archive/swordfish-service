package space.swordfish.user.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.user.service.domain.Team;

@Service
@EnableSqs
public class TeamUserQueueListener {

    private final static String SERVICE = "http://user-service/teams";

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private RestTemplate restTemplate;

    @MessageMapping("${queues.teamEvents}")
    public void instanceCommandHandler(String payload) {
        Team team = jsonTransformService.read(Team.class, payload);

        // Load the JWT into the internal request header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + team.getRequestToken());
        HttpEntity<String> userEntity = new HttpEntity<>(payload, headers);

        // Decide and set where to fire the request to
        String endpoint;
        switch (team.getSwordfishCommand()) {
            case "create": {
                endpoint = "/create";
                break;
            }
            case "update": {
                endpoint = "/update";
                break;
            }
            case "delete": {
                endpoint = "/delete";
                break;
            }
            default: {
                endpoint = "/error";
            }
        }

        // Fire the initial string payload through to the correct controller endpoint
        restTemplate.exchange(SERVICE + endpoint, HttpMethod.POST, userEntity, String.class);
    }
}
