/* Licensed under Apache-2.0 */
package space.swordfish.node.service.services;

import org.springframework.http.ResponseEntity;
import space.swordfish.node.service.domain.Snapshot;

public interface DownloadService {

  ResponseEntity<byte[]> downloadSnapshot(Snapshot snapshot);

  String writeSnapshot(ResponseEntity<byte[]> response, Snapshot snapshot);
}
