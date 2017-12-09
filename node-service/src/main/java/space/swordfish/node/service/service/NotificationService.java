package space.swordfish.node.service.service;

public interface NotificationService {

	void send(String channel, String event, String payload);

}
