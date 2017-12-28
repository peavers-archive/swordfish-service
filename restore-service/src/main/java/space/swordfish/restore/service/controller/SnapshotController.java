package space.swordfish.restore.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import space.swordfish.restore.service.api.snapshot.SilverstripeSnapshot;
import space.swordfish.restore.service.domain.StackEvent;

@RestController
public class SnapshotController {

    private final SilverstripeSnapshot silverstripeSnapshot;

    @Autowired
    public SnapshotController(SilverstripeSnapshot silverstripeSnapshot) {
        this.silverstripeSnapshot = silverstripeSnapshot;
    }

    @GetMapping("/snapshots/{projectId}")
    public JsonNode listAll(@PathVariable String projectId) {
        return silverstripeSnapshot.listAll(projectId).getBody();
    }

    @GetMapping("/snapshots/{projectId}/{snapshotId}")
    public JsonNode view(@PathVariable String projectId,
                         @PathVariable String snapshotId) {
        return silverstripeSnapshot.view(projectId, snapshotId).getBody();
    }

    @PostMapping("/snapshots/create")
    public JsonNode create(@RequestBody StackEvent stackEvent) {
        return silverstripeSnapshot.create(stackEvent.getProjectId(), stackEvent)
                .getBody();
    }

    @DeleteMapping("/snapshots/delete/{projectId}/{snapshotId}")
    public JsonNode delete(@PathVariable String projectId,
                           @PathVariable String snapshotId) {
        return silverstripeSnapshot.delete(projectId, snapshotId).getBody();
    }

    @GetMapping("/snapshots/transfer/{projectId}/{transferId}")
    public JsonNode transfer(@PathVariable String projectId,
                             @PathVariable String transferId) {
        return silverstripeSnapshot.transfer(projectId, transferId).getBody();
    }
}
