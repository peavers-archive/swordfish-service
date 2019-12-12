/* Licensed under Apache-2.0 */
package space.swordfish.node.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.json.services.JsonTransformServiceImpl;
import space.swordfish.common.notification.domain.Notification;
import space.swordfish.node.service.domain.Snapshot;

@Configuration
public class JsonTransformConfig {

  @Bean
  public JsonTransformService jsonTransformService() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

    ResourceConverter resourceConverter =
        new ResourceConverter(objectMapper, Snapshot.class, Notification.class);

    return new JsonTransformServiceImpl(resourceConverter);
  }

  @Bean
  public ResourceConverter ResourceConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

    ResourceConverter converter =
        new ResourceConverter(objectMapper, Snapshot.class, Notification.class);

    converter.disableDeserializationOption(DeserializationFeature.REQUIRE_RESOURCE_ID);

    return converter;
  }
}
