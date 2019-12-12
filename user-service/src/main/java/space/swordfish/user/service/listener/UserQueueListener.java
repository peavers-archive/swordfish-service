/* Licensed under Apache-2.0 */
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
import space.swordfish.user.service.domain.User;

@Service
@EnableSqs
public class UserQueueListener {

  private static final String SERVICE = "http://user-service/users";

  @Autowired private JsonTransformService jsonTransformService;

  @Autowired private RestTemplate restTemplate;

  @MessageMapping("${queues.userEvents}")
  public void instanceCommandHandler(String payload) {
    User user = jsonTransformService.read(User.class, payload);

    // Load the JWT into the internal request header
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + user.getRequestToken());
    HttpEntity<String> userEntity = new HttpEntity<>(payload, headers);

    // Decide and set where to fire the request to
    String endpoint;
    switch (user.getSwordfishCommand()) {
      case "create":
        {
          endpoint = "/create";
          break;
        }
      case "update":
        {
          endpoint = "/update";
          break;
        }
      case "delete":
        {
          endpoint = "/delete";
          break;
        }
      default:
        {
          endpoint = "/error";
        }
    }

    // Fire the initial string payload through to the correct controller endpoint
    restTemplate.exchange(SERVICE + endpoint, HttpMethod.POST, userEntity, String.class);
  }
}
