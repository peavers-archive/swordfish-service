/* Licensed under Apache-2.0 */
package space.swordfish.edge.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.json.services.JsonTransformServiceImpl;
import space.swordfish.edge.service.domain.*;

@Configuration
public class JsonTransformConfig {

  @Bean
  public JsonTransformService jsonTransformService() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

    ResourceConverter resourceConverter =
        new ResourceConverter(
            objectMapper,
            Instance.class,
            StackEvent.class,
            User.class,
            SecurityGroup.class,
            Team.class);

    return new JsonTransformServiceImpl(resourceConverter);
  }

  @Bean
  public ResourceConverter resourceConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

    return new ResourceConverter(
        objectMapper,
        Instance.class,
        StackEvent.class,
        User.class,
        Team.class,
        SecurityGroup.class);
  }
}
