package space.swordfish.instance.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
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

	private final EC2Create ec2Create;
	private final EC2Stop ec2Stop;
	private final EC2Start ec2Start;
	private final EC2Terminate ec2Terminate;
	private final EC2Reboot ec2Reboot;
	private final JsonTransformService jsonTransformService;

	@Autowired
	public QueueListener(EC2Create ec2Create, EC2Stop ec2Stop, EC2Start ec2Start,
			EC2Terminate ec2Terminate, EC2Reboot ec2Reboot,
			JsonTransformService jsonTransformService) {
		this.ec2Create = ec2Create;
		this.ec2Stop = ec2Stop;
		this.ec2Start = ec2Start;
		this.ec2Terminate = ec2Terminate;
		this.ec2Reboot = ec2Reboot;
		this.jsonTransformService = jsonTransformService;
	}

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
