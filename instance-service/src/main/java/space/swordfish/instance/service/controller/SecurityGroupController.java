package space.swordfish.instance.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.instance.service.domain.SecurityGroup;
import space.swordfish.instance.service.service.EC2SecurityGroups;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/security-groups")
public class SecurityGroupController {

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private EC2SecurityGroups ec2SecurityGroups;

    @GetMapping()
    public String findAll() {
        List<SecurityGroup> securityGroups = ec2SecurityGroups.getAll();

        log.info("Security Groups {}", securityGroups);

        return jsonTransformService.writeList(securityGroups);
    }
}