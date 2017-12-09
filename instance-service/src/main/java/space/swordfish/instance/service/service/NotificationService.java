package space.swordfish.instance.service.service;

public interface NotificationService {

	void send(String channel, String event, String payload);

}
