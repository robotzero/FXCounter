package com.king.counter.service;

import com.king.animator.Animator;
import com.king.counter.clock.ClockPresenter;
import com.king.counter.domain.AnimationMetadata;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class Scroller {

    private final Animator animator;
    private BooleanProperty delta = new SimpleBooleanProperty(true);
    private BooleanProperty label = new SimpleBooleanProperty(true);

    private boolean first = true;
    private IntegerProperty seconds = new SimpleIntegerProperty();
    private IntegerProperty minutes = new SimpleIntegerProperty();

    public Scroller(final Animator animator) {
        this.animator = animator;

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
            animator.setRunning();
        } else {
            animator.setMinutesRunning();
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

        List<AnimationMetadata> l = rectangles.stream().map(n -> new AnimationMetadata((Rectangle) n)).collect(Collectors.toList());
        rectangles.stream().filter(r -> r.getTranslateY() == compare).forEach(r -> {
            String id = r.getId();
            Text t = (Text) labels.stream().filter(lbl -> lbl.getId().equals(id)).findFirst().get();
            if (label) {
                t.setText(seconds.get() + "");
            } else {
                t.setText(minutes.get() + "");
            }
        });
        animator.animate(l, deltaY);
        first = false;
    }
}
