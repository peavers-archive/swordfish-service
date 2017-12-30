package space.swordfish.instance.service.service;

import space.swordfish.instance.service.domain.Instance;

public interface EC2KeyPair {

    String create(Instance instance);

    String setName(Instance instance);

    void delete(Instance instance);

}
