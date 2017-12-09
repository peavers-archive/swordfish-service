package space.swordfish.restore.service.api.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SilverstripeLockImpl implements SilverstripeLock {

	@Value("${silverstripe.dashHost}")
	private static String HOST;

	private final RestTemplate restTemplate;

	@Autowired
	public SilverstripeLockImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public ResponseEntity<JsonNode> lock(String projectId, String environmentId) {
		return restTemplate.exchange(
				HOST + "/{projectId}/environment/{environmentId}/lock", HttpMethod.POST,
				null, JsonNode.class, projectId, environmentId);
	}

	@Override
	public ResponseEntity<JsonNode> unlock(String projectId, String environmentId) {
		return restTemplate.exchange(
				HOST + "/{projectId}/environment/{environmentId}/lock", HttpMethod.DELETE,
				null, JsonNode.class, projectId, environmentId);
	}
}
