package space.swordfish.edge.service.service;

import org.springframework.http.HttpEntity;

public interface AuthenticationService {

	HttpEntity<String> addAuthenticationHeader();

	String getCurrentToken();

}
