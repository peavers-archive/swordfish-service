package space.swordfish.edge.service.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;

import java.util.UUID;

@Type("stack-events")
@Data
public class StackEvent {

    @Id
    String id = UUID.randomUUID().toString();

    String instanceId;
    String projectId;
    String mode;
    String environment;
}
