package space.swordfish.edge.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import space.swordfish.edge.service.service.AuthenticationService;

@Slf4j
@Api(tags = "Instances Query")
@RestController
public class InstanceQueryGatewayRestController {

    private final static String SERVICE = "http://instance-service/instances";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AuthenticationService authenticationService;

    @ApiOperation(value = "List all instances and their properties.")
    @GetMapping("/instances")
    public ResponseEntity<String> instances() {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        HttpEntity<String> entity = authenticationService.addAuthenticationHeader();

        return restTemplate.exchange(SERVICE, HttpMethod.GET,
                entity, reference);
    }

    @ApiOperation(value = "List a single instance and it's properties by it's MongoDb ID")
    @GetMapping("/instances/{id}")
    public ResponseEntity<String> viewInstanceById(@PathVariable String id) {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        return restTemplate.exchange(SERVICE + "/{id}", HttpMethod.GET,
                authenticationService.addAuthenticationHeader(), reference, id);
    }
}
