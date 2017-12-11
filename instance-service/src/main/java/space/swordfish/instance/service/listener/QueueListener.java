package space.swordfish.instance.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.service.InstanceEC2Service;

@Slf4j
@Service
@EnableSqs
public class QueueListener {

	/**
	 * Start a instance
	 */
	private static final String START = "start";

	/**
	 * Stop a instance
	 */
	private static final String STOP = "stop";

	/**
	 * Reboot a instance
	 */
	private static final String REBOOT = "reboot";

	/**
	 * Create a new instance
	 */
	private static final String CREATE = "create";

	/**
	 * Terminate a instance
	 */
	private static final String TERMINATE = "terminate";

	/**
	 * Handles actions on preformed on instances
	 */
	private final InstanceEC2Service instanceEC2Service;

	private final JsonTransformService jsonTransformService;

	/**
	 * @param instanceEC2Service InstanceEC2Service for preforming actions on instances
	 */
	@Autowired
	public QueueListener(InstanceEC2Service instanceEC2Service,
			JsonTransformService jsonTransformService) {
		this.instanceEC2Service = instanceEC2Service;
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
			instanceEC2Service.start(instance.getInstanceId());
			break;
		}
		case STOP: {
			instanceEC2Service.stop(instance.getInstanceId());
			break;
		}
		case CREATE: {
			instanceEC2Service.create(instance);
			break;
		}
		case REBOOT: {
			instanceEC2Service.reboot(instance.getInstanceId());
			break;
		}
		case TERMINATE: {
			instanceEC2Service.terminate(instance.getInstanceId());
			break;
		}
		default: {
			log.info("unknown event type {}", instance.getSwordfishCommand());
		}
		}
	}
}
