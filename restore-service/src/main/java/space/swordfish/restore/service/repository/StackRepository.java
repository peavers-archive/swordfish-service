package space.swordfish.restore.service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import space.swordfish.restore.service.domain.Stack;

@RepositoryRestResource
public interface StackRepository extends CrudRepository<Stack, String> {

}