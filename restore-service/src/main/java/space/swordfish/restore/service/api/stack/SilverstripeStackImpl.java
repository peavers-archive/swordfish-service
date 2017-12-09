package space.swordfish.restore.service.api.stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SilverstripeStackImpl implements SilverstripeStack {

	@Value("${silverstripe.dashHost}")
	private static String HOST;

	private final RestTemplate restTemplate;

	@Autowired
	public SilverstripeStackImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public ResponseEntity<JsonNode> listAll() {
		return restTemplate.exchange(HOST + "s", HttpMethod.GET, null, JsonNode.class);
	}

	@Override
	public ResponseEntity<JsonNode> view(String projectId) {
		return restTemplate.exchange(HOST + "/{projectId}/", HttpMethod.GET, null,
				JsonNode.class, projectId);
	}
}
