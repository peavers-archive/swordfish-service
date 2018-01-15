package space.swordfish.user.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.auth0.services.Auth0Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.user.service.domain.Team;
import space.swordfish.user.service.service.TeamService;

@Slf4j
@RestController
@RequestMapping("/teams")
public class TeamCommandController {

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private Auth0Service auth0Service;

    @PostMapping("/create")
    public void create(@RequestBody String payload) {
        Team team = jsonTransformService.read(Team.class, payload);

        String ownerId = auth0Service.getUserIdFromToken(team.getRequestToken());
        team.setOwnerId(ownerId);

        teamService.create(team);
    }

    @PostMapping("/update")
    public void update(@RequestBody String payload) {
        teamService.update(jsonTransformService.read(Team.class, payload));
    }

    @PostMapping("/delete")
    public void delete(@RequestBody String payload) {
        teamService.delete(jsonTransformService.read(Team.class, payload));
    }

}
