package space.swordfish.instance.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.services.sqs.AmazonSQSAsync;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;
import space.swordfish.instance.service.service.Auth0Service;
import space.swordfish.instance.service.service.AuthenticationService;
import space.swordfish.instance.service.service.JsonTransformService;

@Slf4j
@RestController
@RequestMapping("/instances")
public class InstanceController {

	private final InstanceRepository instanceRepository;
	private final JsonTransformService jsonTransformService;
	private final QueueMessagingTemplate queueMessagingTemplate;
	private final Auth0Service auth0Service;
	private final AuthenticationService authenticationService;
	@Value("${queues.instanceEvents}")
	private String queue;

	@Autowired
	public InstanceController(InstanceRepository instanceRepository,
			JsonTransformService jsonTransformService, AmazonSQSAsync amazonSqs,
			Auth0Service auth0Service, AuthenticationService authenticationService) {
		this.instanceRepository = instanceRepository;
		this.jsonTransformService = jsonTransformService;
		this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSqs);
		this.auth0Service = auth0Service;
		this.authenticationService = authenticationService;
	}

	@GetMapping()
	public String findAll() {
		Iterable<Instance> instances = instanceRepository.findAll();

		return jsonTransformService.writeList(instances);
	}

	@GetMapping("me")
	public String findAllByUserId() {
		Iterable<Instance> instances = instanceRepository.findAllByUserId(
				auth0Service.getUserId(authenticationService.getCurrentToken()));

		return jsonTransformService.writeList(instances);
	}

	@GetMapping("{id}")
	public String findById(@PathVariable String id) {
		Instance instance = instanceRepository.findById(id);

		return jsonTransformService.write(instance);
	}

	@PostMapping()
	public String post(@RequestBody String payload) {
		this.queueMessagingTemplate.send(queue,
				MessageBuilder.withPayload(payload).build());
		Instance instance = jsonTransformService.read(Instance.class, payload);

		return jsonTransformService.write(instance);
	}

	@PatchMapping("{id}")
	@DeleteMapping("{id}")
	public String patch(@RequestBody String payload, @PathVariable("id") String id) {
		this.queueMessagingTemplate.send(queue,
				MessageBuilder.withPayload(payload).build());
		Instance instance = jsonTransformService.read(Instance.class, payload);

		return jsonTransformService.write(instance);
	}
}