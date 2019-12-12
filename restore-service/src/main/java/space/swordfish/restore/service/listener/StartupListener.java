/* Licensed under Apache-2.0 */
package space.swordfish.restore.service.listener;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class StartupListener {

  private final AmazonSQSAsync amazonSQSAsync;

  @Value("${queues.restoreEvents}")
  private String events;

  @Autowired
  public StartupListener(AmazonSQSAsync amazonSQSAsync) {
    this.amazonSQSAsync = amazonSQSAsync;
  }

  /** Make sure we've got all the queues we need */
  @EventListener(ApplicationReadyEvent.class)
  public void buildQueues() {
    amazonSQSAsync.createQueue(events);
  }
}
