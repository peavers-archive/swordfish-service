package space.swordfish.edge.service.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;

@Type("stack-events")
@Data
public class StackEvent {

	@Id
	String id;

	String instanceId;
	String projectId;
	String mode;
	String environment;
}
