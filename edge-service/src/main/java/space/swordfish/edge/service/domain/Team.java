/* Licensed under Apache-2.0 */
package space.swordfish.edge.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Type("teams")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team extends BaseDomain {
  String name;
  String owner;

  @Relationship("users")
  List<User> users = new ArrayList<>();
}
