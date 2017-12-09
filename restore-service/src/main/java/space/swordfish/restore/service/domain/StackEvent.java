package space.swordfish.restore.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Type("stack-events")
@Data
@NoArgsConstructor
public class StackEvent {

    @Id
    String id;

    String instanceId;
    String projectId;
    String environment;
    String mode;
}