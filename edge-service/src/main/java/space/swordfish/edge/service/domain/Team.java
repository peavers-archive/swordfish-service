package space.swordfish.edge.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Type("teams")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team extends BaseDomain {
    String name;

    @Relationship("owner")
    User owner;

    @Relationship("members")
    List<User> members;
}
