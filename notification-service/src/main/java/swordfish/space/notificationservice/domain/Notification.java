package swordfish.space.notificationservice.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Type("notifications")
public class Notification {

	@Id
	String id;
	String channel;
	String event;
	String message;

}
