package space.swordfish.instance.service.configuration;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Slf4j
@Configuration
@EnableWebSecurity
@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value(value = "${auth0.audience}")
    private String audience;

    @Value(value = "${auth0.issuer}")
    private String issuer;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.headers().disable();

        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);

        JwtWebSecurityConfigurer.forRS256(audience, issuer).configure(httpSecurity)
                .csrf().disable()
                .authorizeRequests().anyRequest().authenticated();
    }
}