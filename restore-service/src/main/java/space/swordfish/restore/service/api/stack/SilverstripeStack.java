package space.swordfish.restore.service.api.stack;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface SilverstripeStack {

	ResponseEntity<JsonNode> listAll();

	ResponseEntity<JsonNode> view(String projectId);
}
