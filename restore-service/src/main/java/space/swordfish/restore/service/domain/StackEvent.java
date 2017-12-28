package space.swordfish.restore.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Type("stack-events")
@Data
@NoArgsConstructor
public class StackEvent {

    @Id
    String id = UUID.randomUUID().toString();

    String instanceId;
    String projectId;
    String environment;
    String mode;
}