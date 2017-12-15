package swordfish.space.notificationservice.services;

import com.pusher.rest.Pusher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PusherServiceImpl implements PusherService {

	private final Pusher pusher;

	@Autowired
	public PusherServiceImpl(Pusher pusher) {
		this.pusher = pusher;
	}

	@Override
	public void push(String channel, String event, String message) {
		pusher.trigger(channel, event, message);
	}
}
