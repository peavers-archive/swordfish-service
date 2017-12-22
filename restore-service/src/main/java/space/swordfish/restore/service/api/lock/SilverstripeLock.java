package space.swordfish.restore.service.api.lock;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

public interface SilverstripeLock {

	ResponseEntity<JsonNode> lock(String projectId, String environmentId);

	ResponseEntity<JsonNode> unlock(String projectId, String environmentId);

}