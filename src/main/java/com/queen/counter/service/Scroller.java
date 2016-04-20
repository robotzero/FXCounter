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
import org.reactfx.*;

import java.time.LocalTime;
import java.util.List;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCode.T;

public class Scroller {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;
    private BooleanProperty delta = new SimpleBooleanProperty(false);
    private BooleanProperty label = new SimpleBooleanProperty(false);

    private boolean first = true;
    private IntegerProperty offset = new SimpleIntegerProperty(1);
    private IntegerProperty seconds = new SimpleIntegerProperty();
    private IntegerProperty minutes = new SimpleIntegerProperty();

    private final EventSource<Boolean> foundNode = new EventSource<>();

    public Scroller(final Animator animator, final InMemoryCachedServiceLocator cache) {
        this.animator = animator;
        this.cache = cache;

        EventStream lab = EventStreams.changesOf(label);
        EventStream del = EventStreams.changesOf(delta);

//        SuspendableEventStream ehlo = EventStreams.changesOf(delta).suppressible();
//
//        foundNode.suspenderOf(ehlo).map(change -> {
//            System.out.println("BLAH");
//            return 3;
//        }).feedTo(offset);
//
//        ehlo.map(change -> {
//            System.out.println("BLAH2");
//            return 2;
//        }).feedTo(offset);

        EventStream cc = EventStreams.combine(del, foundNode);

        EventStream<?> combined = StateMachine.init(false)
             .on(cc).transition((wasMuted, i) -> true)
             .on(del).emit((muted, t) -> Optional.of(2))
             .toEventStream();

        combined.map(change -> {
            return 2;
        }).feedTo(offset);

        cc.map(change -> {
            return 3;
        }).feedTo(offset);
//        EventStreams.combine(lab, foundNode).map(change -> {
//            System.out.println("1. one");
//            return 3;
//        }).feedTo(offset);
//        EventStreams.changesOf(delta).or(lab).map(change -> {
//            System.out.println("2. two");
//            return 2;
//        }).feedTo(offset);

    }


    public void scroll(final List<Node> rectangles, final List<Text> labels, double deltaY) {

        final int found = deltaY > 0 ? 0 : 240;
        final int compare = deltaY < 0 ? 0 : 240;
        rectangles.stream().filter(r -> r.getTranslateY() == found).findAny().ifPresent(r -> foundNode.push(true));
        delta.set(deltaY < 0);
        this.label.set(rectangles.get(0).getId().contains("seconds"));

        int offsetNumber = offset.getValue();
        System.out.println(offsetNumber);
        if (label.get()) {
            animator.setRunning(true);
        } else {
            animator.setMinutesRunning(true);
        }

        if (delta.get()) {
            if (label.get()) {
                ClockPresenter.userTimeSeconds = ClockPresenter.userTimeSeconds.minusSeconds(offsetNumber);
            } else {
                ClockPresenter.userTimeMinutes = ClockPresenter.userTimeMinutes.minusMinutes(offsetNumber);
            }
        } else {
            if (label.get()) {
                ClockPresenter.userTimeSeconds = ClockPresenter.userTimeSeconds.plusSeconds(offsetNumber);
            } else {
                ClockPresenter.userTimeMinutes = ClockPresenter.userTimeMinutes.plusMinutes(offsetNumber);
            }
        }
        if (label.get()) {
            seconds.set(ClockPresenter.userTimeSeconds.getSecond());
        } else {
            minutes.set(ClockPresenter.userTimeMinutes.getMinute());
        }

        ClockPresenter.userTime = ClockPresenter.userTime.withSecond(ClockPresenter.userTimeSeconds.getSecond()).withMinute(ClockPresenter.userTimeMinutes.getMinute());

        List<AnimationMetadata> l = rectangles.stream().map(n -> (AnimationMetadata) cache.get(AnimationMetadata.class, n)).collect(Collectors.toList());

        rectangles.stream().filter(r -> r.getTranslateY() == compare).findAny().ifPresent(r -> {
            labels.stream().filter(lbl -> lbl.getId().equals(r.getId())).findFirst().ifPresent(lbl -> {
                if (label.get()) {
                    lbl.setText(seconds.get() + "");
                } else {
                    lbl.setText(minutes.get() + "");
                }
            });
        });

        animator.animate(l, deltaY, cache);
        offset.set(1);
    }

    private LocalTime clockTick(LocalTime current, Function<LocalTime, LocalTime> tick) {
        return tick.apply(current);
    }

    public void setFirst() {
        first = true;
    }
}
