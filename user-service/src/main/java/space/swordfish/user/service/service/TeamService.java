/* Licensed under Apache-2.0 */
package space.swordfish.user.service.service;

import space.swordfish.user.service.domain.Team;

public interface TeamService {

  void create(Team team);

  void update(Team team);

  void delete(Team team);
}
