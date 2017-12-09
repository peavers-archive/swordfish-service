package space.swordfish.node.service.service;

public interface QueueMessageService {

	void send(String queue, String payload);
}
