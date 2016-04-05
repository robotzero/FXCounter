package com.queen.animator;

import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.AnimationMetadata;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

import java.util.List;

public class Animator {

    private TranslateTransition currentTransition;
    private BooleanProperty isRunning = new SimpleBooleanProperty(false);
    private BooleanProperty isMinutesRunning = new SimpleBooleanProperty(false);
    private BooleanProperty ticking = new SimpleBooleanProperty(false);
    private String currentLabel;

    public void animate(List<AnimationMetadata> l, double delta, InMemoryCachedServiceLocator cache) {
        l.stream().map(ll -> {
            if (ll.getRectangle().getId().contains("seconds")) {
                isRunning.set(true);
            } else {
                isMinutesRunning.set(true);
            }
            TranslateTransition t = (TranslateTransition) cache.get(TranslateTransition.class, ll.getRectangle());
            t.setFromY(ll.getFrom(delta).getY());
            t.setToY(ll.getTo(delta).getY());
            t.setInterpolator(Interpolator.EASE_IN);
            currentTransition = t;
            currentLabel = ll.getRectangle().getId();
            return t;
        }).forEach(Animation::play);

        currentTransition.setOnFinished(event -> {
            if (!ticking.get()) {
                if (currentLabel.contains("seconds")) {
                    isRunning.set(false);
                } else {
                    isMinutesRunning.set(false);
                }
            }
        });
    }

    public BooleanProperty isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning.set(running);
    }

    public void setMinutesRunning(boolean running) {
        isMinutesRunning.set(running);
    }
    public BooleanProperty isMinutesRunning() {
        return isMinutesRunning;
    }

    public void setTicking(boolean ticking) {
        this.ticking.set(ticking);
    }

    public BooleanProperty isTicking() {
        return this.ticking;
    }
}
