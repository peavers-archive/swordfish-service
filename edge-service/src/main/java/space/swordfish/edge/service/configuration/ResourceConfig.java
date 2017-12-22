package space.swordfish.edge.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.ResourceConverter;

import space.swordfish.edge.service.domain.Instance;
import space.swordfish.edge.service.domain.StackEvent;

@Configuration
public class ResourceConfig {

	@Bean
	public ResourceConverter resourceConverter() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

		return new ResourceConverter(objectMapper, Instance.class, StackEvent.class);
	}

}
