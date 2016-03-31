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
    private boolean first = true;
    private IntegerProperty clock = new SimpleIntegerProperty();

    public Scroller(final Animator animator) {
        this.animator = animator;

        delta.addListener((observable, oldValue, newValue) -> {
            first = true;
        });
    }

    public void scroll(final List<Node> rectangles, final List<Node> labels, double deltaY) {
        final int compare;
        if (rectangles.get(0).getId().contains("seconds")) {
            animator.setRunning();
        } else {
            animator.setMinutesRunning();
        }
        delta.set(deltaY < 0);
        if (delta.get()) {
            ClockPresenter.userTime = ClockPresenter.userTime.minusSeconds(1);
            if (first) {
                ClockPresenter.time = ClockPresenter.userTime.minusSeconds(1);
            } else {
                ClockPresenter.time = ClockPresenter.time.minusSeconds(1);
            }
            compare = 0;
        } else {
            ClockPresenter.userTime = ClockPresenter.userTime.plusSeconds(1);
            if (first) {
                ClockPresenter.time = ClockPresenter.userTime.plusSeconds(1);
            } else {
                ClockPresenter.time = ClockPresenter.time.plusSeconds(1);
            }
            compare = 240;
        }
        clock.set(ClockPresenter.time.getSecond());
        List<AnimationMetadata> l = rectangles.stream().map(n -> new AnimationMetadata((Rectangle) n)).collect(Collectors.toList());
        rectangles.stream().filter(r -> r.getTranslateY() == compare).forEach(r -> {
            String id = r.getId();
            Text t = (Text) labels.stream().filter(lbl -> lbl.getId().equals(id)).findFirst().get();
            t.setText(clock.get() + "");
        });
        animator.animate(l, deltaY);
        first = false;
    }
}
