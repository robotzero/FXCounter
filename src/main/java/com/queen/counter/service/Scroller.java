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
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Scroller {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;
    private BooleanProperty delta = new SimpleBooleanProperty(true);
    private BooleanProperty label = new SimpleBooleanProperty(true);

    private boolean first = true;
    private IntegerProperty seconds = new SimpleIntegerProperty();
    private IntegerProperty minutes = new SimpleIntegerProperty();
    private LocalTime mainClock = LocalTime.of(0, 12, 12);

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

    public void scroll(final List<Node> rectangles, final List<Text> labels, double deltaY) {
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
            mainClock = clockTick(mainClock, clock -> clock.minusSeconds(1));
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
            mainClock = clockTick(mainClock, clock -> clock.plusSeconds(1));
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
        System.out.println(mainClock);
        ClockPresenter.userTime = ClockPresenter.userTime.withSecond(ClockPresenter.userTimeSeconds.getSecond()).withMinute(ClockPresenter.userTimeMinutes.getMinute());

        List<AnimationMetadata> l = rectangles.stream().map(n -> (AnimationMetadata) cache.get(AnimationMetadata.class, n)).collect(Collectors.toList());

        rectangles.stream().filter(r -> r.getTranslateY() == compare).findAny().ifPresent(r -> {
            labels.stream().filter(lbl -> lbl.getId().equals(r.getId())).findFirst().ifPresent(lbl -> {
                if (label) {
                    lbl.setText(seconds.get() + "");
                    System.out.println(seconds.get());
                    System.out.println(mainClock.minusSeconds(2));
                } else {
                    lbl.setText(minutes.get() + "");
                }
            });
        });

        animator.animate(l, deltaY, cache);
        first = false;
    }

    private LocalTime clockTick(LocalTime current, Function<LocalTime, LocalTime> tick) {
        return tick.apply(current);
    }

    public void setFirst() {
        first = true;
    }
}
