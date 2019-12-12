/* Licensed under Apache-2.0 */
package space.swordfish.edge.service.controller.restore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.auth.services.AuthenticationService;

@RestController
public class RestoreQueryGatewayRestController {

  private static final String SERVICE = "http://restore-service";

  @Autowired private RestTemplate restTemplate;

  @Autowired private AuthenticationService authenticationService;

  @GetMapping("/restore/deployments/{projectId}/{environmentId}")
  public ResponseEntity<String> listAllDeployments(
      @PathVariable String projectId, @PathVariable String environmentId) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/deployments/{projectId}/{environmentId}",
        HttpMethod.GET,
        null,
        reference,
        projectId,
        environmentId);
  }

  @GetMapping("/restore/deployments/{projectId}/{environmentId}/{deploymentId}")
  public ResponseEntity<String> viewDeployments(
      @PathVariable String projectId,
      @PathVariable String environmentId,
      @PathVariable String deploymentId) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/deployments/{projectId}/{environmentId}/{deploymentId}",
        HttpMethod.GET,
        null,
        reference,
        projectId,
        environmentId,
        deploymentId);
  }

  @GetMapping("/stacks")
  public ResponseEntity<String> listAllStacks() {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/stacks/",
        HttpMethod.GET,
        authenticationService.addAuthenticationHeader(),
        reference);
  }

  @GetMapping("/stacks/{projectId}")
  public ResponseEntity<String> viewStack(@PathVariable String projectId) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/stacks/{projectId}",
        HttpMethod.GET,
        authenticationService.addAuthenticationHeader(),
        reference,
        projectId);
  }

  @GetMapping("/restore/stacks/{projectId}")
  public ResponseEntity<String> viewStacks(@PathVariable String projectId) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/stacks/{projectId}", HttpMethod.GET, null, reference, projectId);
  }

  @GetMapping("/restore/snapshots/{projectId}")
  public ResponseEntity<String> listAllSnapshots(@PathVariable String projectId) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/snapshots/{projectId}", HttpMethod.GET, null, reference, projectId);
  }

  @GetMapping("/restore/snapshots/{projectId}/{snapshotId}")
  public ResponseEntity<String> viewSnapshots(
      @PathVariable String projectId, @PathVariable String snapshotId) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/snapshots/{projectId}/{snapshotId}",
        HttpMethod.GET,
        null,
        reference,
        projectId,
        snapshotId);
  }

  @GetMapping("/restore/snapshots/transfer/{projectId}/{transferId}")
  public ResponseEntity<String> transferSnapshots(
      @PathVariable String projectId, @PathVariable String transferId) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/snapshots/transfer/{projectId}/{transferId}",
        HttpMethod.GET,
        null,
        reference,
        projectId,
        transferId);
  }

  @GetMapping("/restore/git/{projectId}/{fetchId}")
  public ResponseEntity<String> viewFetch(
      @PathVariable("projectId") String projectId, @PathVariable("fetchId") String fetchId) {
    ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};

    return restTemplate.exchange(
        SERVICE + "/restore/git/{projectId}/{fetchId}",
        HttpMethod.GET,
        null,
        reference,
        projectId,
        fetchId);
  }
}
