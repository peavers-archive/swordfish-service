package space.swordfish.restore.service.service;

import java.util.List;

import com.github.jasminb.jsonapi.JSONAPIDocument;

public interface JsonTransformService {

	<T> T read(Class<T> cls, String payload);

	<T> JSONAPIDocument<List<T>> readList(Class<T> cls, String payload);

	String write(Object object);

	String writeList(Iterable<?> iterable);

}
