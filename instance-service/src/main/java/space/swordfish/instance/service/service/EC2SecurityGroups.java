/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import java.util.List;
import space.swordfish.instance.service.domain.SecurityGroup;

public interface EC2SecurityGroups {

  List<SecurityGroup> getAll();
}
