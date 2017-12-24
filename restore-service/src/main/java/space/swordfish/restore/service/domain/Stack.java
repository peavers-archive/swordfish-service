package space.swordfish.restore.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

@Type("stacks")
@Data
@NoArgsConstructor
public class Stack {

	@Id
	String id;
	String name;
	String title;
	String created;

    @JsonProperty("created_unix")
    String createdUnix;
}
