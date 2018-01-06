package space.swordfish.instance.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.instance.service.repository.InstanceRepository;
import space.swordfish.instance.service.service.EC2Sync;

@Slf4j
@RestController
@RequestMapping("/instances")
public class InstanceQueryController {

    @Autowired
    private EC2Sync ec2Sync;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private InstanceRepository instanceRepository;

    @GetMapping()
    public String findAll() {
        return jsonTransformService.writeList(ec2Sync.getAll());
    }

    @GetMapping("{id}")
    public String findById(@PathVariable String id) {
        return jsonTransformService.write(instanceRepository.findByInstanceId(id));
    }
}