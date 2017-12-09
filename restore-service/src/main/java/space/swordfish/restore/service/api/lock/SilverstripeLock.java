package space.swordfish.restore.service.api.lock;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface SilverstripeLock {

    ResponseEntity<JsonNode> lock(String projectId, String environmentId);

    ResponseEntity<JsonNode> unlock(String projectId, String environmentId);

}