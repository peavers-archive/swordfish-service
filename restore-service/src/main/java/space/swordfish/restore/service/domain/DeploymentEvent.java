package space.swordfish.restore.service.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeploymentEvent {
	String ref;
	String ref_type;
	String title;
	String summary;
	String bypass_and_start;
}
