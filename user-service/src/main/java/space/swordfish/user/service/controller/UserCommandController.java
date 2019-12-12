/* Licensed under Apache-2.0 */
package space.swordfish.user.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.user.service.domain.User;
import space.swordfish.user.service.service.UserService;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserCommandController {

  @Autowired private JsonTransformService jsonTransformService;

  @Autowired private UserService userService;

  @PostMapping("/create")
  public void create(@RequestBody String payload) {
    userService.create(jsonTransformService.read(User.class, payload));
  }

  @PostMapping("/update")
  public void update(@RequestBody String payload) {
    userService.update(jsonTransformService.read(User.class, payload));
  }

  @PostMapping("/delete")
  public void delete(@RequestBody String payload) {
    userService.delete(jsonTransformService.read(User.class, payload));
  }
}
