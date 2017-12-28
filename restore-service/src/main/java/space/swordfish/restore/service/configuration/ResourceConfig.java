package space.swordfish.restore.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.notification.domain.Notification;
import space.swordfish.restore.service.domain.Snapshot;
import space.swordfish.restore.service.domain.Stack;
import space.swordfish.restore.service.domain.StackEvent;
import space.swordfish.restore.service.domain.Transfer;

@Configuration
public class ResourceConfig {

    @Bean
    public ResourceConverter resourceConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        return new ResourceConverter(objectMapper, Snapshot.class, Stack.class, StackEvent.class, Transfer.class,
                Notification.class);
    }

}
