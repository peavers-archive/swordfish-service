/* Licensed under Apache-2.0 */
package space.swordfish.user.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.json.services.JsonTransformServiceImpl;
import space.swordfish.common.notification.domain.Notification;
import space.swordfish.user.service.domain.Team;
import space.swordfish.user.service.domain.User;

@Configuration
public class JsonTransformConfig {

  @Bean
  public JsonTransformService jsonTransformService() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

    ResourceConverter resourceConverter =
        new ResourceConverter(objectMapper, User.class, Team.class, Notification.class);

    return new JsonTransformServiceImpl(resourceConverter);
  }

  @Bean
  public ResourceConverter resourceConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

    return new ResourceConverter(objectMapper, User.class, Team.class, Notification.class);
  }
}
