package swordfish.space.notificationservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.ResourceConverter;

import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.json.services.JsonTransformServiceImpl;
import space.swordfish.common.notification.domain.Notification;

@Configuration
public class JsonTransformConfig {

	@Bean
	public JsonTransformService jsonTransformService() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

		ResourceConverter resourceConverter = new ResourceConverter(objectMapper,
				Notification.class);

		return new JsonTransformServiceImpl(resourceConverter);
	}

	@Bean
	public ResourceConverter resourceConverter() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

		return new ResourceConverter(objectMapper, Notification.class);
	}

}