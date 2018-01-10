package space.swordfish.user.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.user.service.repositoriy.UserRepository;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserQueryController {

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public String findAll() {
        return jsonTransformService.writeList(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable String id) {
        return jsonTransformService.write(userRepository.findById(id));
    }
}