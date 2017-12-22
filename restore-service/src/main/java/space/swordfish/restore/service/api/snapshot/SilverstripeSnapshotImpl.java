package space.swordfish.restore.service.api.snapshot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import space.swordfish.restore.service.domain.StackEvent;

@Service
public class SilverstripeSnapshotImpl implements SilverstripeSnapshot {

	private final RestTemplate restTemplate;
	@Value("${silverstripe.dashHost}")
	private String HOST;

	@Autowired
	public SilverstripeSnapshotImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public ResponseEntity<JsonNode> listAll(String projectId) {
		return restTemplate.exchange(HOST + "/{projectId}/snapshots", HttpMethod.GET,
				null, JsonNode.class, projectId);
	}

	@Override
	public ResponseEntity<JsonNode> view(String projectId, String snapshotId) {
		return restTemplate.exchange(HOST + "/{projectId}/snapshots/{snapshotId}",
				HttpMethod.GET, null, JsonNode.class, projectId, snapshotId);
	}

	@Override
	public ResponseEntity<JsonNode> create(String projectId, StackEvent stackEvent) {
		return restTemplate.exchange(HOST + "/{projectId}/snapshots", HttpMethod.POST,
				new HttpEntity<>(stackEvent), JsonNode.class, stackEvent.getProjectId());
	}

	@Override
	public ResponseEntity<JsonNode> delete(String projectId, String snapshotId) {
		return restTemplate.exchange(HOST + "/{projectId}/snapshots/{snapshotId}",
				HttpMethod.DELETE, null, JsonNode.class, projectId, snapshotId);
	}

	@Override
	public ResponseEntity<JsonNode> transfer(String projectId, String transferId) {
		return restTemplate.exchange(
				HOST + "/{projectId}/snapshots/transfer/{transferId}", HttpMethod.GET,
				null, JsonNode.class, projectId, transferId);
	}
}
