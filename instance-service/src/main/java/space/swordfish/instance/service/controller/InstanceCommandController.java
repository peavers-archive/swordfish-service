/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.service.*;

@Slf4j
@RestController
@RequestMapping("/instances")
public class InstanceCommandController {

  @Autowired private EC2Create ec2Create;

  @Autowired private EC2Start ec2Start;

  @Autowired private EC2Stop ec2Stop;

  @Autowired private EC2Reboot ec2Reboot;

  @Autowired private EC2Terminate ec2Terminate;

  @Autowired private JsonTransformService jsonTransformService;

  @PostMapping("/create")
  public void create(@RequestBody String payload) {
    ec2Create.process(jsonTransformService.read(Instance.class, payload));
  }

  @PostMapping("/start")
  public void start(@RequestBody String payload) {
    ec2Start.process(jsonTransformService.read(Instance.class, payload));
  }

  @PostMapping("/stop")
  public void stop(@RequestBody String payload) {
    ec2Stop.process(jsonTransformService.read(Instance.class, payload));
  }

  @PostMapping("/reboot")
  public void reboot(@RequestBody String payload) {
    ec2Reboot.process(jsonTransformService.read(Instance.class, payload));
  }

  @PostMapping("/terminate")
  public void terminate(@RequestBody String payload) {
    ec2Terminate.process(jsonTransformService.read(Instance.class, payload));
  }
}
