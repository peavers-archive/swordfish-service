package space.swordfish.restore.service.api.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.swordfish.restore.service.domain.DeploymentEvent;
import space.swordfish.restore.service.service.AuthenticatedRestTemplate;

@Service
public class SilverstripeDeploymentImpl implements SilverstripeDeployment {

    @Value("${silverstripe.dashHost}")
    private String HOST;

    @Autowired
    private AuthenticatedRestTemplate authenticatedRestTemplate;

    @Override
    public ResponseEntity<JsonNode> listAll(String projectId, String environmentId) {
        return authenticatedRestTemplate.restTemplate().exchange(
                HOST + "/{projectId}/environment/{environmentId}/deploys", HttpMethod.GET,
                null, JsonNode.class, projectId, environmentId);
    }

    @Override
    public ResponseEntity<JsonNode> create(String projectId, String environmentId,
                                           DeploymentEvent deploymentEvent) {
        return authenticatedRestTemplate.restTemplate().exchange(
                HOST + "/{projectId}/environment/{environmentId}/deploys",
                HttpMethod.POST, new HttpEntity<>(deploymentEvent), JsonNode.class,
                projectId, environmentId);
    }

    @Override
    public ResponseEntity<JsonNode> view(String projectId, String environmentId,
                                         String deploymentId) {
        return authenticatedRestTemplate.restTemplate().exchange(
                HOST + "/{projectId}/environment/{environmentId}/deploys/{deploymentId}",
                HttpMethod.GET, null, JsonNode.class, projectId, environmentId,
                deploymentId);
    }

    @Override
    public ResponseEntity<JsonNode> delete(String projectId, String environmentId,
                                           String deploymentId) {
        return authenticatedRestTemplate.restTemplate().exchange(
                HOST + "/{projectId}/environment/{environmentId}/deploys/{deploymentId}",
                HttpMethod.DELETE, null, JsonNode.class, projectId, environmentId,
                deploymentId);
    }
}
