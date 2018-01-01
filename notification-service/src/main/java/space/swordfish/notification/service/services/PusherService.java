package space.swordfish.notification.service.services;

public interface PusherService {

	void push(String channel, String event, String message);
}
