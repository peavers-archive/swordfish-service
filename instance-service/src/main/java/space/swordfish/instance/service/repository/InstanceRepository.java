package space.swordfish.instance.service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import space.swordfish.instance.service.domain.Instance;

@RepositoryRestResource
public interface InstanceRepository extends CrudRepository<Instance, String> {

	Instance findByInstanceId(String instanceId);

	Iterable<Instance> findAllByStateAndProduction(String state, boolean production);

	Instance findById(String id);

	void deleteByInstanceId(String instanceId);

	Iterable<Instance> findAllByState(String terminated);

	Iterable<Instance> findAllByUserId(String userId);
}
