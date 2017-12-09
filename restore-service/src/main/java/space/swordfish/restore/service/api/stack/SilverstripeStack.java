package space.swordfish.restore.service.api.stack;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

public interface SilverstripeStack {

	ResponseEntity<JsonNode> listAll();

	ResponseEntity<JsonNode> view(String projectId);
}
