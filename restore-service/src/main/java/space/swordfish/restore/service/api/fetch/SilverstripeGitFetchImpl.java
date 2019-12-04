/* Licensed under Apache-2.0 */
package space.swordfish.restore.service.api.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.swordfish.restore.service.service.AuthenticatedRestTemplate;

@Service
public class SilverstripeGitFetchImpl implements SilverstripeGitFetch {

  @Value("${silverstripe.dashHost}")
  private String HOST;

  @Autowired private AuthenticatedRestTemplate authenticatedRestTemplate;

  @Override
  public ResponseEntity<JsonNode> create(String projectId) {
    return authenticatedRestTemplate
        .restTemplate()
        .exchange(
            HOST + "/{projectId}/git/fetches", HttpMethod.POST, null, JsonNode.class, projectId);
  }

  @Override
  public ResponseEntity<JsonNode> view(String projectId, String fetchId) {
    return authenticatedRestTemplate
        .restTemplate()
        .exchange(
            HOST + "/{projectId}/git/fetches/{fetchId}",
            HttpMethod.GET,
            null,
            JsonNode.class,
            projectId,
            fetchId);
  }
}
