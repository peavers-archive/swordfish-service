package space.swordfish.restore.service.service;

interface NotificationService {

    void send(String channel, String event, String payload);

}
