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

    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String REBOOT = "reboot";
    private static final String CREATE = "create";
    private static final String TERMINATE = "terminate";

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
    @MessageMapping(value = "${queues.instanceEvents}")
    public void instanceCommandHandler(String payload) {

        log.info("Payload {}", payload);

        Instance instance = jsonTransformService.read(Instance.class, payload);

        if (instance.getSwordfishCommand() == null) {
            log.warn("incorrect object passed in {}", instance);
            return;
        }

        switch (instance.getSwordfishCommand()) {
            case START: {
                ec2Start.start(instance.getInstanceId());
                break;
            }
            case STOP: {
                ec2Stop.stop(instance.getInstanceId());
                break;
            }
            case CREATE: {
                ec2Create.create(instance);
                break;
            }
            case REBOOT: {
                ec2Reboot.reboot(instance.getInstanceId());
                break;
            }
            case TERMINATE: {
                ec2Terminate.terminate(instance.getInstanceId());
                break;
            }
            default: {
                log.info("unknown event type {}", instance.getSwordfishCommand());
            }
        }
    }
}
