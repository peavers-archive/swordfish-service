package space.swordfish.instance.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;
import space.swordfish.instance.service.service.EC2Sync;

@Slf4j
@Service
class StartupListener {

	private final EC2Sync ec2Sync;
	private final InstanceRepository repository;

	@Autowired
	public StartupListener(EC2Sync ec2Sync, InstanceRepository repository) {
		this.ec2Sync = ec2Sync;
		this.repository = repository;
	}

	/**
	 * Populates a database with new instances from AWS. This is a one-way sync.
	 * <p>
	 * Primarily for development purposes. CRAIG!
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void syncInstances() {
		log.info("syncing instances from aws...");

		ec2Sync.syncAll();
	}

	/**
	 * Remove dead instances from storage.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void removeDeadInstances() {
		log.info("deleting terminated instances from storage...");

		Iterable<Instance> instances = repository.findAllByState("terminated");

		repository.delete(instances);
	}
}
