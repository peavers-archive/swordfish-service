package space.swordfish.instance.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;
import space.swordfish.instance.service.service.InstanceEC2Service;

@Slf4j
@Service
class StartupListener {

    private InstanceEC2Service instanceEC2Service;
    private InstanceRepository repository;

    @Autowired
    public StartupListener(InstanceEC2Service instanceEC2Service, InstanceRepository repository) {
        this.instanceEC2Service = instanceEC2Service;
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

        instanceEC2Service.syncAll();
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
