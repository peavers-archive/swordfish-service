package space.swordfish.user.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.user.service.domain.Team;
import space.swordfish.user.service.domain.User;
import space.swordfish.user.service.repositoriy.TeamRepository;
import space.swordfish.user.service.repositoriy.UserRepository;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/teams")
public class TeamQueryController {

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public String findAll() {
        return jsonTransformService.writeList(teamRepository.findAll());
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable String id) {
        Team team = teamRepository.findById(id);

        team.setMembers((List<User>) userRepository.findAllByTeam(team));

        return jsonTransformService.write(team);
    }
}