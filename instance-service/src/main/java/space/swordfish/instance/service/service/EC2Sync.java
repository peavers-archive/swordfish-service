package space.swordfish.instance.service.service;

import space.swordfish.instance.service.domain.Instance;

public interface EC2Sync {

    Iterable<Instance> getAll();

    Instance getByInstanceId(String instanceId);
}
