/* Licensed under Apache-2.0 */
package space.swordfish.restore.service.service;

import java.util.List;
import java.util.concurrent.Future;
import space.swordfish.restore.service.domain.Stack;
import space.swordfish.restore.service.domain.StackEvent;

public interface SilverstripeService {

  List<Stack> getAllStacks();

  Future<String> createSnapshot(String projectId, StackEvent stackEvent);
}
