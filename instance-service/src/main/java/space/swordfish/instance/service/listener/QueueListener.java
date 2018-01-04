package space.swordfish.instance.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.service.*;

@Slf4j
@Service
@EnableSqs
public class QueueListener {

    @Autowired
    private EC2Create ec2Create;

    @Autowired
    private EC2Stop ec2Stop;

    @Autowired
    private EC2Start ec2Start;

    @Autowired
    private EC2Terminate ec2Terminate;

    @Autowired
    private EC2Reboot ec2Reboot;

    @Autowired
    private JsonTransformService jsonTransformService;

    /**
     * Reads events from InstanceQueue and determines what to do with them.
     *
     * @param payload String representation of a InstanceEvent
     */
    @MessageMapping("${queues.instanceEvents}")
    public void instanceCommandHandler(String payload) {
        Instance instance = jsonTransformService.read(Instance.class, payload);

        switch (instance.getSwordfishCommand()) {
            case "start": {
                ec2Start.process(instance);
                break;
            }
            case "stop": {
                ec2Stop.process(instance);
                break;
            }
            case "create": {
                ec2Create.process(instance);
                break;
            }
            case "reboot": {
                ec2Reboot.process(instance);
                break;
            }
            case "terminate": {
                ec2Terminate.process(instance);
                break;
            }
        }
    }
}
