package space.swordfish.instance.service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import space.swordfish.instance.service.domain.Instance;

@RepositoryRestResource
public interface InstanceRepository extends CrudRepository<Instance, String> {

    Instance findByInstanceId(String instanceId);

    Instance findByKeyName(String keyName);

    Instance findById(String id);

    Iterable<Instance> findAllByStateAndProduction(String state, boolean production);

    Iterable<Instance> findAllByStateAndSwordfishIsTrueAndProductionIsFalse(String state);

    void deleteByInstanceId(String instanceId);

    Iterable<Instance> findAllByState(String terminated);

    Iterable<Instance> findAllByUserId(String userId);
}
