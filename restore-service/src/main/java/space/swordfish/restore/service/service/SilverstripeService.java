package space.swordfish.restore.service.service;

import space.swordfish.restore.service.domain.Stack;
import space.swordfish.restore.service.domain.StackEvent;

import java.util.List;
import java.util.concurrent.Future;

public interface SilverstripeService {

    List<Stack> getAllStacks();

    Future<String> createSnapshot(String projectId, StackEvent stackEvent);
}
