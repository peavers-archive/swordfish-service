package space.swordfish.restore.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QueueMessageServiceImpl implements QueueMessageService {

    private final QueueMessagingTemplate queueMessagingTemplate;

    private final AmazonSQSAsync amazonSQSAsync;

    @Autowired
    public QueueMessageServiceImpl(@Qualifier("amazonSQSAsync") AmazonSQSAsync amazonSqs) {
        this.amazonSQSAsync = amazonSqs;
        this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSqs);
    }

    @Override
    public void send(String queue, String payload) {
        amazonSQSAsync.createQueueAsync(queue, new AsyncHandler<CreateQueueRequest, CreateQueueResult>() {
            @Override
            public void onError(Exception exception) {
                log.warn("something went wrong with the queue creation {}", exception.getLocalizedMessage());
            }

            @Override
            public void onSuccess(CreateQueueRequest request, CreateQueueResult createQueueResult) {
                queueMessagingTemplate.send( queue, MessageBuilder.withPayload(payload).build());
            }
        });
    }
}
