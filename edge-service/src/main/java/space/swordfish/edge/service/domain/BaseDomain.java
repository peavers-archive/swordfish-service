/* Licensed under Apache-2.0 */
package space.swordfish.edge.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class BaseDomain {

  @Id String id;
  String requestToken;
  String swordfishCommand;
}
