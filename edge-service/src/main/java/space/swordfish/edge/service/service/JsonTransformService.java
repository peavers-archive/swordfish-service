package space.swordfish.edge.service.service;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

import java.util.List;

public interface JsonTransformService {

    <T> T read(Class<T> cls, String payload);

    <T> JSONAPIDocument<List<T>> readList(Class<T> cls, String payload);

    String write(Object object) throws DocumentSerializationException;

    String writeList(Iterable<?> iterable) throws DocumentSerializationException;

}
