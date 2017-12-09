package space.swordfish.restore.service.api.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import space.swordfish.restore.service.domain.DeploymentEvent;

public interface SilverstripeDeployment {

    ResponseEntity<JsonNode> listAll(String projectId, String environmentId);

    ResponseEntity<JsonNode> create(String projectId, String environmentId, DeploymentEvent deploymentEvent);

    ResponseEntity<JsonNode> view(String projectId, String environmentId, String deploymentId);

    ResponseEntity<JsonNode> delete(String projectId, String environmentId, String deploymentId);

}
