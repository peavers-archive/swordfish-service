package space.swordfish.edge.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.ResourceConverter;

import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.json.services.JsonTransformServiceImpl;
import space.swordfish.edge.service.domain.Instance;
import space.swordfish.edge.service.domain.StackEvent;

@Configuration
public class JsonTransformConfig {

	@Bean
	public JsonTransformService jsonTransformService() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

		ResourceConverter resourceConverter = new ResourceConverter(objectMapper,
				Instance.class, StackEvent.class);

		return new JsonTransformServiceImpl(resourceConverter);
	}

}