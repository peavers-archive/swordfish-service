/* Licensed under Apache-2.0 */
package space.swordfish.node.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.node.service.domain.Snapshot;
import space.swordfish.node.service.services.DownloadService;

@Slf4j
@Service
@EnableSqs
public class QueueListener {

  @Autowired private DownloadService downloadService;

  @Autowired private JsonTransformService jsonTransformService;

  @Autowired private NotificationService notificationService;

  @MessageMapping("${aws.channel}")
  public void instanceCommandHandler(String payload) {
    Snapshot snapshot = jsonTransformService.read(Snapshot.class, payload);

    if (downloadService.writeSnapshot(downloadService.downloadSnapshot(snapshot), snapshot)
        != null) {
      notificationService.send(
          "restore_event", "download_success", jsonTransformService.write(snapshot));

    } else {
      notificationService.send(
          "restore_event", "download_error", jsonTransformService.write(snapshot));
    }
  }
}
