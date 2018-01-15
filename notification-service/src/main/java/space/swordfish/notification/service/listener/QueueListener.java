package space.swordfish.notification.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.domain.Notification;
import space.swordfish.notification.service.services.PusherService;

@Service
@EnableSqs
public class QueueListener {

    @Autowired
    private PusherService pusher;

    @Autowired
    private JsonTransformService jsonTransformService;

    @MessageMapping("${queues.notificationEvents}")
    public void instanceCommandHandler(String payload) {
        Notification notification = jsonTransformService.read(Notification.class,
                payload);

        pusher.push(notification.getChannel(), notification.getEvent(),
                notification.getMessage());
    }
}
