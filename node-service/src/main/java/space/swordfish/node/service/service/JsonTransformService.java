package space.swordfish.node.service.service;

import com.github.jasminb.jsonapi.JSONAPIDocument;

import java.util.List;

public interface JsonTransformService {

    <T> T read(Class<T> cls, String payload);

    <T> JSONAPIDocument<List<T>> readList(Class<T> cls, String payload);

    String write(Object object);

    String writeList(Iterable<?> iterable);

}
