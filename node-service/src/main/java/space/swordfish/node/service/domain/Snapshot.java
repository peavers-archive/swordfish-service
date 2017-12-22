package space.swordfish.node.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;

import lombok.Builder;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Type("snapshots")
@Data
@Builder
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