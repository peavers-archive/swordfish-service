package space.swordfish.restore.service.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

@Type("snapshots")
@Data
@NoArgsConstructor
public class Snapshot {

	@Id
	String id;

	String created;
	String mode;
	String size;
	String stackId;
	String channel;

	@Links
	com.github.jasminb.jsonapi.Links links;

}