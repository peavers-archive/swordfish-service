/* Licensed under Apache-2.0 */
package space.swordfish.user.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.user.service.domain.Team;
import space.swordfish.user.service.repositoriy.TeamRepository;
import space.swordfish.user.service.service.TeamService;

@Slf4j
@RestController
@RequestMapping("/teams")
public class TeamCommandController {

  @Autowired private JsonTransformService jsonTransformService;

  @Autowired private TeamService teamService;

  @Autowired private TeamRepository teamRepository;

  @PostMapping("/create")
  public void create(@RequestBody String payload) {
    Team team = jsonTransformService.read(Team.class, payload);

    teamService.create(team);
  }

  @PostMapping("/update")
  public void update(@RequestBody String payload) {
    teamService.update(jsonTransformService.read(Team.class, payload));
  }

  @PostMapping("/delete")
  public void delete(@RequestBody String payload) {
    Team team = jsonTransformService.read(Team.class, payload);
    Team realTeam = teamRepository.findById(team.getId());

    teamService.delete(realTeam);
  }
}
