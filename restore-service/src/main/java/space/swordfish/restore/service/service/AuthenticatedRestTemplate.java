package space.swordfish.restore.service.service;

import org.springframework.web.client.RestTemplate;

public interface AuthenticatedRestTemplate {

    RestTemplate restTemplate();

}
