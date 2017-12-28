package space.swordfish.restore.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.restore.service.api.fetch.SilverstripeGitFetch;

@RestController
public class FetchController {

    private final SilverstripeGitFetch silverstripeGitFetch;

    @Autowired
    public FetchController(SilverstripeGitFetch silverstripeGitFetch) {
        this.silverstripeGitFetch = silverstripeGitFetch;
    }

    @PostMapping("/git/{projectId}")
    public JsonNode create(@PathVariable("projectId") String projectId) {
        return silverstripeGitFetch.create(projectId).getBody();
    }

    @GetMapping("/git/{projectId}/{fetchId}")
    public JsonNode view(@PathVariable("projectId") String projectId,
                         @PathVariable("fetchId") String fetchId) {
        return silverstripeGitFetch.view(projectId, fetchId).getBody();
    }
}