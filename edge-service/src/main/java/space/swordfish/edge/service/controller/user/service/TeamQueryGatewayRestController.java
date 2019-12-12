/* Licensed under Apache-2.0 */
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
import space.swordfish.common.auth.services.AuthenticationService;

@Slf4j
@RestController
public class TeamQueryGatewayRestController {

  private static final String SERVICE = "http://user-service/teams";

  @Autowired private AuthenticationService authenticationService;

  @LoadBalanced @Autowired private RestTemplate restTemplate;

  @GetMapping("/teams")
  public ResponseEntity<String> teams() {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE, HttpMethod.GET, authenticationService.addAuthenticationHeader(), reference);
  }

  @GetMapping("/teams/{id}")
  public ResponseEntity<String> teamsById(@PathVariable String id) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/{id}",
        HttpMethod.GET,
        authenticationService.addAuthenticationHeader(),
        reference,
        id);
  }
}
