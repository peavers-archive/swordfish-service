package space.swordfish.instance.service.service;

import space.swordfish.instance.service.domain.Instance;

public interface InstanceEC2Service {

	void start(String instanceId);

	void stop(String instanceId);

	void reboot(String instanceId);

	void terminate(String instanceId);

	void create(Instance instance);

	void syncAll();
}
