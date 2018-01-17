package space.swordfish.user.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.common.auth0.services.Auth0Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.user.service.domain.User;
import space.swordfish.user.service.repositoriy.UserRepository;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserQueryController {

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private Auth0Service auth0Service;

    @GetMapping()
    public String findAll() {
        return jsonTransformService.writeList(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable String id) {
        User user = userRepository.findById(id);

        String idFromToken = auth0Service.getUserIdFromToken(authenticationService.getCurrentToken());
        com.auth0.json.mgmt.users.User auth0ServiceUser = auth0Service.getUser(idFromToken);

        if (user == null) {
            user = new User();
        }

        user.setId(auth0ServiceUser.getId());
        user.setEmail(auth0ServiceUser.getEmail());
        user.setFamilyName(auth0ServiceUser.getFamilyName());
        user.setGivenName(auth0ServiceUser.getGivenName());
        user.setNickName(auth0ServiceUser.getNickname());
        user.setName(auth0ServiceUser.getName());
        user.setPicture(auth0ServiceUser.getPicture());
        user.setUsername(auth0ServiceUser.getUsername());

        userRepository.save(user);

        log.info("Updated User {}", user);

        return jsonTransformService.write(user);
    }
}