package com.king.animator;

import com.king.counter.domain.AnimationMetadata;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.util.List;

public class Animator {

    public void animate(List<AnimationMetadata> l, double delta) {
        l.stream().map(ll -> {
            TranslateTransition t = new TranslateTransition(Duration.millis(600), ll.getRectangle());
            t.setFromY(ll.getFrom(delta).getY());
            t.setToY(ll.getTo(delta).getY());
            t.setInterpolator(Interpolator.EASE_IN);
            return t;
        }).forEach(Animation::play);
    }
}
