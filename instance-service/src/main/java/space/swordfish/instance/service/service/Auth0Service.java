package space.swordfish.instance.service.service;

public interface Auth0Service {

	String getUserId(String token);

	String getUserName(String userId);

	String getUserProfilePicture(String userId);

}
