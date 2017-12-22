package space.swordfish.edge.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.common.auth.services.AuthenticationServiceImpl;

@Configuration
public class AuthenticationConfig {

	@Bean
	public AuthenticationService authenticationService() {
		return new AuthenticationServiceImpl();
	}

}
