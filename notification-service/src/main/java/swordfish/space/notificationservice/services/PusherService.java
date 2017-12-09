package swordfish.space.notificationservice.services;

public interface PusherService {

    public void push(String channel, String event, String message);
}
