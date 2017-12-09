package swordfish.space.notificationservice.services;

public interface PusherService {

	void push(String channel, String event, String message);
}
