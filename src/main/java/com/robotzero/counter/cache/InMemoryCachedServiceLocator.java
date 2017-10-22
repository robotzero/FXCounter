package com.robotzero.counter.cache;

import com.robotzero.counter.domain.AnimationMetadata;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.Set;

import static com.googlecode.totallylazy.Sets.set;

public class InMemoryCachedServiceLocator implements ServiceLocator {

    private Set<Object> objectReferences = set();

    @Override
    public Object get(Class clazz, Object reference) throws ServiceConfigurationError {
        if (clazz.getName().contains("AnimationMetadata")) {
            if (objectReferences.stream().anyMatch(o -> o.getClass().getName().contains("AnimationMetadata"))) {
                Optional optional = objectReferences.stream().filter(o -> {
                    if (o.getClass().getName().contains("AnimationMetadata")) {
                        AnimationMetadata am = (AnimationMetadata) o;
                        return am.getRectangle().equals(reference);
                    }
                    return false;
                }).findFirst();

                if (optional.isPresent()) {
                    return optional.get();
                }
            }

            AnimationMetadata metadata = new AnimationMetadata((Rectangle) reference);
            objectReferences.add(metadata);
            return metadata;
        }

        if (clazz.getName().contains("TranslateTransition")) {
            if (objectReferences.stream().anyMatch(o -> o.getClass().getName().contains("TranslateTransition"))) {
                Optional optional = objectReferences.stream().filter(o -> {
                    if (o.getClass().getName().contains("TranslateTransition")) {
                        TranslateTransition trt = (TranslateTransition) o;
                        return trt.getNode().equals(reference);
                    }
                    return false;
                }).findFirst();

                if (optional.isPresent()) {
                    return optional.get();
                }
            }

            TranslateTransition trt = new TranslateTransition(Duration.millis(600), (Rectangle) reference);
            objectReferences.add(trt);
            return trt;
        }

        throw new ServiceConfigurationError("Class does not exists.");
    }
}
