package space.swordfish.instance.service.repository;

import space.swordfish.instance.service.domain.Instance;

public interface InstanceRepositoryCustomSave {

    void saveAndNotify(Instance instance);

}
