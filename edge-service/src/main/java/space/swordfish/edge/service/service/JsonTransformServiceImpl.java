package space.swordfish.edge.service.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

import space.swordfish.edge.service.domain.Instance;
import space.swordfish.edge.service.domain.StackEvent;

@Service
public class JsonTransformServiceImpl implements JsonTransformService {

	private ResourceConverter converter;

	JsonTransformServiceImpl() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

		this.converter = new ResourceConverter(objectMapper, Instance.class,
				StackEvent.class);

		converter
				.disableDeserializationOption(DeserializationFeature.REQUIRE_RESOURCE_ID);
	}

	@Override
	public <T> T read(Class<T> cls, String payload) {
		return converter.readDocument(payload.getBytes(), cls).get();
	}

	@Override
	public <T> JSONAPIDocument<List<T>> readList(Class<T> cls, String payload) {
		return converter.readDocumentCollection(payload.getBytes(), cls);
	}

	@Override
	public String write(Object object) throws DocumentSerializationException {
		JSONAPIDocument<Object> document = new JSONAPIDocument<>(object);

		byte[] bytes = converter.writeDocument(document);

		return new String(bytes);
	}

	@Override
	public String writeList(Iterable<?> iterable) throws DocumentSerializationException {
		JSONAPIDocument<Collection<?>> listJSONAPIDocument = new JSONAPIDocument<>(
				(Collection<?>) iterable);

		byte[] bytes = converter.writeDocumentCollection(listJSONAPIDocument);

		return new String(bytes);
	}
}