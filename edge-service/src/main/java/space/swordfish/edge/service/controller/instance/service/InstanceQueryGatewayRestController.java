package space.swordfish.edge.service.controller.instance.service;

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
import space.swordfish.common.auth.services.AuthenticationService;

@Slf4j
@RestController
public class InstanceQueryGatewayRestController {

    private final static String SERVICE = "http://instance-service/instances";

    @LoadBalanced
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/instances")
    public ResponseEntity<String> instances() {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        return restTemplate.exchange(SERVICE, HttpMethod.GET, authenticationService.addAuthenticationHeader(), reference);
    }

    @GetMapping("/instances/refresh-all")
    public ResponseEntity<String> refreshAll() {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        return restTemplate.exchange(SERVICE + "/refresh-all", HttpMethod.GET, authenticationService.addAuthenticationHeader(), reference);
    }

    @GetMapping("/instances/{id}")
    public ResponseEntity<String> viewInstanceById(@PathVariable String id) {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        return restTemplate.exchange(SERVICE + "/{id}", HttpMethod.GET,
                authenticationService.addAuthenticationHeader(), reference, id);
    }


}
