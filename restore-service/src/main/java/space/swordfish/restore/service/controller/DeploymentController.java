/* Licensed under Apache-2.0 */
package space.swordfish.restore.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import space.swordfish.restore.service.api.deployment.SilverstripeDeployment;
import space.swordfish.restore.service.domain.DeploymentEvent;

@RestController
public class DeploymentController {

  private final SilverstripeDeployment silverstripeDeployment;

  @Autowired
  public DeploymentController(SilverstripeDeployment silverstripeDeployment) {
    this.silverstripeDeployment = silverstripeDeployment;
  }

  @GetMapping("/deployments/{projectId}/{environmentId}")
  public JsonNode listAll(@PathVariable String projectId, @PathVariable String environmentId) {
    return silverstripeDeployment.listAll(projectId, environmentId).getBody();
  }

  @PostMapping("/deployments/{projectId}/{environmentId}")
  public JsonNode create(
      @PathVariable String projectId,
      @PathVariable String environmentId,
      @RequestBody DeploymentEvent deploymentEvent) {
    return silverstripeDeployment.create(projectId, environmentId, deploymentEvent).getBody();
  }

  @GetMapping("/deployments/{projectId}/{environmentId}/{deploymentId}")
  public JsonNode view(
      @PathVariable String projectId,
      @PathVariable String environmentId,
      @PathVariable String deploymentId) {
    return silverstripeDeployment.view(projectId, environmentId, deploymentId).getBody();
  }

  @DeleteMapping("/deployments/{projectId}/{environmentId}/{deploymentId}")
  public JsonNode delete(
      @PathVariable String projectId,
      @PathVariable String environmentId,
      @PathVariable String deploymentId) {
    return silverstripeDeployment.delete(projectId, environmentId, deploymentId).getBody();
  }
}
