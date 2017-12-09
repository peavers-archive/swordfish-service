package space.swordfish.instance.service.service;

import com.auth0.spring.security.api.authentication.JwtAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public HttpEntity<String> addAuthenticationHeader() {
        String token = getCurrentToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        return new HttpEntity<String>("parameters", headers);
    }

    @Override
    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication) {

            log.info("Current Token {}", ((JwtAuthentication) authentication).getToken());

            return ((JwtAuthentication) authentication).getToken();
        }

        return null;
    }
}
