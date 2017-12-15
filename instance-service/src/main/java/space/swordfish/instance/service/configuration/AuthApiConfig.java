package space.swordfish.instance.service.configuration;

import com.auth0.client.auth.AuthAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthApiConfig {

	@Value("${auth0.issuer}")
	private String domain;

	@Value("${auth0.clientId}")
	private String clientId;

	@Value("${auth0.clientSecret}")
	private String clientSecret;

	@Bean
	public AuthAPI authAPI() {
		return new AuthAPI(domain, clientId, clientSecret);
	}
}
