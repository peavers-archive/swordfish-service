package space.swordfish.restore.service.api.stack;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.swordfish.restore.service.service.AuthenticatedRestTemplate;

@Slf4j
@Service
public class SilverstripeStackImpl implements SilverstripeStack {

    @Value("${silverstripe.dashHost}")
    private String HOST;

    @Autowired
    private AuthenticatedRestTemplate authenticatedRestTemplate;

    @Override
    public ResponseEntity<JsonNode> listAll() {
        return authenticatedRestTemplate.restTemplate().exchange(HOST + "s", HttpMethod.GET, null, JsonNode.class);
    }

    @Override
    public ResponseEntity<JsonNode> view(String projectId) {
        return authenticatedRestTemplate.restTemplate().exchange(HOST + "/{projectId}/", HttpMethod.GET, null,
                JsonNode.class, projectId);
    }
}
