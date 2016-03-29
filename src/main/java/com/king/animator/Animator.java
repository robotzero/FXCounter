package com.king.animator;

import com.king.counter.domain.AnimationMetadata;
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

    public void animate(List<AnimationMetadata> l, double delta) {
        l.stream().map(ll -> {
            isRunning.set(true);
            TranslateTransition t = new TranslateTransition(Duration.millis(600), ll.getRectangle());
            t.setFromY(ll.getFrom(delta).getY());
            t.setToY(ll.getTo(delta).getY());
            t.setInterpolator(Interpolator.EASE_IN);
            currentTransition = t;
            return t;
        }).forEach(Animation::play);

        currentTransition.setOnFinished(event -> {
            isRunning.set(false);
        });
    }

    public BooleanProperty isRunning() {
        return isRunning;
    }

    public void setRunning() {
        isRunning.set(true);
    }
}
