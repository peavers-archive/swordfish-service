package space.swordfish.restore.service.configuration;

import org.springframework.context.annotation.Configuration;
import space.swordfish.common.queue.services.QueueMessageService;
import space.swordfish.common.queue.services.QueueMessageServiceImpl;

@Configuration
public class QueueConfig {

    public QueueMessageService queueMessageService() {
        return new QueueMessageServiceImpl();
    }

}
