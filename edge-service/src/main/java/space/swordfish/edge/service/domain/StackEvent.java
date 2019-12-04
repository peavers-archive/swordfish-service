/* Licensed under Apache-2.0 */
package space.swordfish.edge.service.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.UUID;
import lombok.Data;

@Type("stack-events")
@Data
public class StackEvent {

  @Id String id = UUID.randomUUID().toString();

  String instanceId;
  String projectId;
  String mode;
  String environment;

  String userToken;
}
