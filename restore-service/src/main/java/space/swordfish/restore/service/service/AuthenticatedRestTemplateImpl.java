package space.swordfish.restore.service.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.auth.domain.User;
import space.swordfish.common.auth.services.AuthenticationService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AuthenticatedRestTemplateImpl implements AuthenticatedRestTemplate {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public RestTemplate restTemplate() {
        User currentUser = authenticationService.getCurrentUser();

        String username = currentUser.getSilverstripeUsername();
        String token = currentUser.getSilverstripeToken();

        if (username == null || token == null) {
            return null;
        }

        return new BasicAuthRestTemplate(username, token);
    }
}

class BasicAuthRestTemplate extends RestTemplate {

    private String username;
    private String password;

    BasicAuthRestTemplate(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        addAuthentication();
    }

    private void addAuthentication() {
        if (StringUtils.isEmpty(username)) {
            throw new RuntimeException("Username is mandatory for Basic Auth");
        }

        List<ClientHttpRequestInterceptor> interceptors = Collections
                .singletonList(new BasicAuthInterceptor(username, password));
        setRequestFactory(new InterceptingClientHttpRequestFactory(getRequestFactory(),
                interceptors));
    }
}

class BasicAuthInterceptor implements ClientHttpRequestInterceptor {
    private String username;
    private String password;

    BasicAuthInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private static String encodeCredentialsForBasicAuth(String username,
                                                        String password) {
        return "Basic "
                + new Base64().encodeToString((username + ":" + password).getBytes());
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,
                encodeCredentialsForBasicAuth(username, password));

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}