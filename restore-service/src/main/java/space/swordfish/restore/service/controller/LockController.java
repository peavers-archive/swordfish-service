package space.swordfish.restore.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.restore.service.api.lock.SilverstripeLock;

@RestController
public class LockController {

    private final SilverstripeLock silverstripeLock;

    @Autowired
    public LockController(SilverstripeLock silverstripeLock) {
        this.silverstripeLock = silverstripeLock;
    }

    @PostMapping("/lock/{projectId}/{environmentId}")
    public JsonNode lock(@PathVariable("projectId") String projectId, @PathVariable("environmentId") String environmentId) {
        return silverstripeLock.lock(projectId, environmentId).getBody();
    }

    @DeleteMapping("/lock/{projectId}/{environmentId}")
    public JsonNode unlock(@PathVariable("projectId") String projectId, @PathVariable("environmentId") String environmentId) {
        return silverstripeLock.unlock(projectId, environmentId).getBody();
    }
}