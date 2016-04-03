package com.queen.counter.cache;

import com.queen.counter.domain.AnimationMetadata;
import javafx.scene.shape.Rectangle;

import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.Set;

import static com.googlecode.totallylazy.Sets.set;

public class InMemoryCachedServiceLocator implements ServiceLocator {

    private Set<Object> objectReferences = set();

    @Override
    public Object get(Class clazz, Object reference) throws ServiceConfigurationError {
        if (clazz.getName().contains("AnimationMetadata")) {
            if (!objectReferences.isEmpty()) {
                Optional optional = objectReferences.stream().filter(o -> {
                   AnimationMetadata am = (AnimationMetadata) o;
                    return am.getRectangle().equals(reference);
                }).findFirst();

                if (optional.isPresent()) {
                    return optional.get();
                }
            }

            AnimationMetadata metadata = new AnimationMetadata((Rectangle) reference);
            objectReferences.add(metadata);
            return new AnimationMetadata((Rectangle) reference);
        }

        throw new ServiceConfigurationError("Class does not exists.");
    }
}
