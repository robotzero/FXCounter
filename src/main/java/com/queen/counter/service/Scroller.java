package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.clock.ClockPresenter;
import com.queen.counter.domain.AnimationMetadata;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Scroller {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;
    private BooleanProperty delta = new SimpleBooleanProperty(true);
    private BooleanProperty label = new SimpleBooleanProperty(true);

    private boolean first = true;
    private IntegerProperty seconds = new SimpleIntegerProperty();
    private IntegerProperty minutes = new SimpleIntegerProperty();

    public Scroller(final Animator animator, final InMemoryCachedServiceLocator cache) {
        this.animator = animator;
        this.cache = cache;

        delta.addListener((observable, oldValue, newValue) -> {
            first = true;
        });

        label.addListener((observable, oldValue, newValue) -> {
            first = true;
        });
    }

    public void scroll(final List<Node> rectangles, final List<Node> labels, double deltaY) {
        final int compare;
        final boolean label = rectangles.get(0).getId().contains("seconds");
        this.label.set(label);
        if (label) {
            animator.setRunning(true);
        } else {
            animator.setMinutesRunning(true);
        }
        delta.set(deltaY < 0);
        if (delta.get()) {
            if (label) {
                ClockPresenter.userTimeSeconds = ClockPresenter.userTimeSeconds.minusSeconds(1);
            } else {
                ClockPresenter.userTimeMinutes = ClockPresenter.userTimeMinutes.minusMinutes(1);
            }
            if (first) {
                if (label) {
                    ClockPresenter.time = ClockPresenter.userTimeSeconds.minusSeconds(1);
                } else {
                    ClockPresenter.minutesTime = ClockPresenter.userTimeMinutes.minusMinutes(1);
                }
            } else {
                if (label) {
                    ClockPresenter.time = ClockPresenter.time.minusSeconds(1);
                } else {
                    ClockPresenter.minutesTime = ClockPresenter.minutesTime.minusMinutes(1);
                }
            }
            compare = 0;
        } else {
            if (label) {
                ClockPresenter.userTimeSeconds = ClockPresenter.userTimeSeconds.plusSeconds(1);
            } else {
                ClockPresenter.userTimeMinutes = ClockPresenter.userTimeMinutes.plusMinutes(1);
            }
            if (first) {
                if (label) {
                    ClockPresenter.time = ClockPresenter.userTimeSeconds.plusSeconds(1);
                } else {
                    ClockPresenter.minutesTime = ClockPresenter.userTimeMinutes.plusMinutes(1);
                }
            } else {
                if (label) {
                    ClockPresenter.time = ClockPresenter.time.plusSeconds(1);
                } else {
                    ClockPresenter.minutesTime = ClockPresenter.minutesTime.plusMinutes(1);
                }
            }
            compare = 240;
        }
        if (label) {
            seconds.set(ClockPresenter.time.getSecond());
        } else {
            minutes.set(ClockPresenter.minutesTime.getMinute());
        }

        ClockPresenter.userTime = ClockPresenter.userTime.withSecond(ClockPresenter.userTimeSeconds.getSecond()).withMinute(ClockPresenter.userTimeMinutes.getMinute());

        List<AnimationMetadata> l = rectangles.stream().map(n -> (AnimationMetadata) cache.get(AnimationMetadata.class, n)).collect(Collectors.toList());

        Optional op = rectangles.stream().filter(r -> r.getTranslateY() == compare).findAny();
        if (op.isPresent()) {
            String id = ((Rectangle) op.get()).getId();
            Text t = (Text) labels.stream().filter(lbl -> lbl.getId().equals(id)).findFirst().get();
            if (label) {
                t.setText(seconds.get() + "");
            } else {
                t.setText(minutes.get() + "");
            }
        }

        animator.animate(l, deltaY, cache);
        first = false;
    }

    public void setFirst() {
        first = true;
    }
}
