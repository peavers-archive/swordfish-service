package space.swordfish.restore.service.configuration;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationServiceImpl;

@Configuration
public class NotificationConfig {

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    @Bean
    public NotificationServiceImpl notificationService() {
        return new NotificationServiceImpl(queueMessagingTemplate, amazonSQSAsync, (space.swordfish.common.notification.services.JsonTransformService) jsonTransformService);
    }

}
