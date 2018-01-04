package space.swordfish.instance.service.configuration;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Slf4j
@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value(value = "${auth0.audience}")
	private String audience;

	@Value(value = "${auth0.issuer}")
	private String issuer;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		JwtWebSecurityConfigurer.forRS256(audience, issuer).configure(httpSecurity)
				.authorizeRequests().antMatchers("/health").permitAll();
	}
}