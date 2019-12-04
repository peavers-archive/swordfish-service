/* Licensed under Apache-2.0 */
package space.swordfish.restore.service.api.lock;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.swordfish.restore.service.service.AuthenticatedRestTemplate;

@Service
public class SilverstripeLockImpl implements SilverstripeLock {

  @Value("${silverstripe.dashHost}")
  private String HOST;

  @Autowired private AuthenticatedRestTemplate authenticatedRestTemplate;

  @Override
  public ResponseEntity<JsonNode> lock(String projectId, String environmentId) {
    return authenticatedRestTemplate
        .restTemplate()
        .exchange(
            HOST + "/{projectId}/environment/{environmentId}/lock",
            HttpMethod.POST,
            null,
            JsonNode.class,
            projectId,
            environmentId);
  }

  @Override
  public ResponseEntity<JsonNode> unlock(String projectId, String environmentId) {
    return authenticatedRestTemplate
        .restTemplate()
        .exchange(
            HOST + "/{projectId}/environment/{environmentId}/lock",
            HttpMethod.DELETE,
            null,
            JsonNode.class,
            projectId,
            environmentId);
  }
}
