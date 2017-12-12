package space.swordfish.restore.service.api.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface SilverstripeGitFetch {

	ResponseEntity<JsonNode> create(String projectId);

	ResponseEntity<JsonNode> view(String projectId, String fetchId);
}
