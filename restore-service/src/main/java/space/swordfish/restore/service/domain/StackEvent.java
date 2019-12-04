/* Licensed under Apache-2.0 */
package space.swordfish.restore.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Type("stack-events")
@Data
@NoArgsConstructor
public class StackEvent {

  @Id String id = UUID.randomUUID().toString();

  String instanceId;
  String projectId;
  String environment;
  String mode;

  String userToken;
}
