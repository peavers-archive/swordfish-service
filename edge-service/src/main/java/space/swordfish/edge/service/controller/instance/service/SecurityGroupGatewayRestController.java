package space.swordfish.edge.service.controller.instance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.auth.services.AuthenticationService;

@Slf4j
@RestController
public class SecurityGroupGatewayRestController {

    private final static String SERVICE = "http://instance-service/security-groups";

    @LoadBalanced
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/security-groups")
    public ResponseEntity<String> securityGroups() {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        return restTemplate.exchange(SERVICE, HttpMethod.GET, authenticationService.addAuthenticationHeader(), reference);
    }

}
