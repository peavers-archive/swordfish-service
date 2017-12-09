package space.swordfish.restore.service.api.fetch;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

public interface SilverstripeGitFetch {

	ResponseEntity<JsonNode> create(String projectId);

	ResponseEntity<JsonNode> view(String projectId, String fetchId);
}
