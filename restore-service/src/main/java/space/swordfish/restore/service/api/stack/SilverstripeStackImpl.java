package space.swordfish.restore.service.api.stack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
        try {
            return authenticatedRestTemplate.restTemplate().exchange(HOST + "s", HttpMethod.GET, null, JsonNode.class);
        } catch (Exception e) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jNode = objectMapper.createObjectNode();

            return new ResponseEntity<JsonNode>(jNode, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<JsonNode> view(String projectId) {
        return authenticatedRestTemplate.restTemplate().exchange(HOST + "/{projectId}/", HttpMethod.GET, null,
                JsonNode.class, projectId);
    }
}
