package space.swordfish.instance.service.configuration;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value(value = "${auth0.audience}")
    private String audience;

    @Value(value = "${auth0.issuer}")
    private String issuer;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        JwtWebSecurityConfigurer
                .forRS256(audience, issuer)
                .configure(httpSecurity)
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated();
    }
}