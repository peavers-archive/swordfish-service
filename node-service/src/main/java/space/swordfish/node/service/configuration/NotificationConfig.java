package space.swordfish.node.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import space.swordfish.common.notification.services.NotificationServiceImpl;

@Configuration
public class NotificationConfig {

	@Bean
	public NotificationServiceImpl notificationService() {
		return new NotificationServiceImpl();
	}

}
