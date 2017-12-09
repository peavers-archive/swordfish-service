package space.swordfish.restore.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQSAsync;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.restore.service.domain.Notification;

@Slf4j
@Service
class NotificationServiceImpl implements NotificationService {

	private final QueueMessagingTemplate queueMessagingTemplate;
	private final JsonTransformService jsonTransformService;
	@Value("${queues.notificationEvents}")
	private String queue;

	@Autowired
	public NotificationServiceImpl(QueueMessagingTemplate queueMessagingTemplate,
			AmazonSQSAsync amazonSQSAsync, JsonTransformService jsonTransformService) {
		this.queueMessagingTemplate = queueMessagingTemplate;
		this.jsonTransformService = jsonTransformService;

		amazonSQSAsync.createQueueAsync(queue);
	}

	@Override
	public void send(String channel, String event, String payload) {
		Notification notification = new Notification();
		notification.setChannel(channel);
		notification.setEvent(event);
		notification.setMessage(payload);

		Message<String> message = MessageBuilder
				.withPayload(jsonTransformService.write(notification)).build();

		queueMessagingTemplate.send(queue, message);
	}
}
