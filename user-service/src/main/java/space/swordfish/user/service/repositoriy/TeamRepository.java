/* Licensed under Apache-2.0 */
package space.swordfish.user.service.repositoriy;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import space.swordfish.user.service.domain.Team;

@RepositoryRestResource
public interface TeamRepository extends CrudRepository<Team, String> {

  Team findById(String id);
}
