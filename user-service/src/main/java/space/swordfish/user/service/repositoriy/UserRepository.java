/* Licensed under Apache-2.0 */
package space.swordfish.user.service.repositoriy;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import space.swordfish.user.service.domain.Team;
import space.swordfish.user.service.domain.User;

@RepositoryRestResource
public interface UserRepository extends MongoRepository<User, String> {

  User findById(String id);

  Iterable<User> findAllByTeam(Team team);
}
