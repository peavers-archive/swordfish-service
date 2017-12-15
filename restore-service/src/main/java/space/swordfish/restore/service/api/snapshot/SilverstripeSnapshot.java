package space.swordfish.restore.service.api.snapshot;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import space.swordfish.restore.service.domain.StackEvent;

public interface SilverstripeSnapshot {

	ResponseEntity<JsonNode> listAll(String projectId);

	ResponseEntity<JsonNode> view(String projectId, String snapshotId);

	ResponseEntity<JsonNode> create(String projectId, StackEvent stackEvent);

	ResponseEntity<JsonNode> delete(String projectId, String snapshotId);

	ResponseEntity<JsonNode> transfer(String projectId, String transferId);
}
