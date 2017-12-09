package space.swordfish.node.service.listener;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.node.service.service.NotificationService;

@Slf4j
@Service
@EnableSqs
public class QueueListener {

	/**
	 * Name of the queue to read messages from
	 */
	private static final String QUEUE_NAME = "SnapshotRestoreQueue-i-0a2e6744e7ebc12b8";

	private final RestTemplate restTemplate;
	private final NotificationService notificationService;

	@Autowired
	public QueueListener(RestTemplate restTemplate,
			NotificationService notificationService) {
		this.restTemplate = restTemplate;
		this.notificationService = notificationService;
	}

	/**
	 * Reads events from InstanceQueue and determines what to do with them.
	 *
	 * @param payload String representation of a InstanceEvent
	 */
	@MessageMapping(QUEUE_NAME)
	public void instanceCommandHandler(String payload) {
		log.info("download link {}", payload);

		downloadFile(payload);
	}

	private void downloadFile(String link) {
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<byte[]> response = restTemplate.exchange(link, HttpMethod.GET,
				entity, byte[].class, "1");

		notificationService.send("restore_event", "restore_info",
				"Node service downloading snapshot...");

		if (response.getStatusCode() == HttpStatus.OK) {
			try {
				Files.write(Paths.get("/tmp/test.sspak"), response.getBody());
				notificationService.send("restore_event", "restore_success",
						"Node service snapshot downloaded");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}