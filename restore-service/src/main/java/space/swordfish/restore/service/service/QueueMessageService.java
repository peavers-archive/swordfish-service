package space.swordfish.restore.service.service;

public interface QueueMessageService {

    void send(String queue, String payload);
}
