package space.swordfish.instance.service.service;

import org.springframework.http.HttpEntity;

public interface AuthenticationService {

    HttpEntity<String> addAuthenticationHeader();

    String getCurrentToken();

}
