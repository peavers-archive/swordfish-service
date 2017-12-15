package space.swordfish.node.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.ResourceConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.node.service.domain.Snapshot;

@Configuration
public class ResourceConverterConfig {

	@Bean
	public ResourceConverter ResourceConverter() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

		ResourceConverter converter = new ResourceConverter(objectMapper, Snapshot.class);

		converter
				.disableDeserializationOption(DeserializationFeature.REQUIRE_RESOURCE_ID);

		return converter;
	}

}
