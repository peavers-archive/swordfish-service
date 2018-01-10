package space.swordfish.edge.service.controller.user.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class UserQueryGatewayRestController {

    private final static String SERVICE = "http://user-service/users";

    @LoadBalanced
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/users")
    public ResponseEntity<String> users() {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        return restTemplate.exchange(SERVICE, HttpMethod.GET, null, reference);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<String> userById(@PathVariable String id) {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        return restTemplate.exchange(SERVICE + "/{id}", HttpMethod.GET,
                null, reference, id);
    }
}
