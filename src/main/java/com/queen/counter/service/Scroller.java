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
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple3;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Scroller {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;
    private BooleanProperty delta = new SimpleBooleanProperty(false);
    private BooleanProperty label = new SimpleBooleanProperty(false);
    private BooleanProperty f = new SimpleBooleanProperty(false);

    private IntegerProperty offset = new SimpleIntegerProperty(1);
    private IntegerProperty seconds = new SimpleIntegerProperty();
    private IntegerProperty minutes = new SimpleIntegerProperty();
    private LocalTime userTimeSeconds = LocalTime.of(0, 0, 0);
    private LocalTime userTimeMinutes = LocalTime.of(0, 0, 0);


    public Scroller(final Animator animator, final InMemoryCachedServiceLocator cache) {
        this.animator = animator;
        this.cache = cache;

        EventStream lab = EventStreams.valuesOf(label);
        EventStream del = EventStreams.valuesOf(delta);
        EventStream ff = EventStreams.valuesOf(f);

        EventStream<Tuple3<Boolean, Boolean, Boolean>> combo = EventStreams.combine(del, ff, lab);

        combo.map(change -> {
            Boolean delta = change.get1();
            Boolean found = change.get2();
            Boolean labl = change.get3();
            if ((delta && found) || (labl && found)) {
                return 2;
            }

            return 1;
        }).feedTo(offset);
    }

    public void scroll(final List<Node> rectangles, final List<Text> labels, double deltaY) {

        int normalizedDelta = (int) deltaY / (int) Math.abs(deltaY);
        final int found = deltaY > 0 ? 0 : 240;
        final int compare = deltaY < 0 ? 0 : 240;
        rectangles.stream().filter(r -> r.getTranslateY() == found).findAny().ifPresent(r -> f.set(true));
        delta.set(deltaY < 0);

        this.label.set(rectangles.get(0).getId().contains("seconds"));
        int offsetNumber = offset.getValue();

        if (label.get()) {
            animator.setRunning(true);
        } else {
            animator.setMinutesRunning(true);
        }

        if (label.get()) {
            userTimeSeconds = userTimeSeconds.plusSeconds(normalizedDelta);
        } else {
            userTimeMinutes = userTimeMinutes.plusMinutes(normalizedDelta);
        }

        if (label.get()) {
            LocalTime l = LocalTime.of(23, 12, userTimeSeconds.plusSeconds(offsetNumber *  normalizedDelta).getSecond());
            seconds.set(l.getSecond());
        } else {
            LocalTime l = LocalTime.of(23, userTimeMinutes.plusMinutes(offsetNumber *  normalizedDelta).getMinute(), 59);
            minutes.set(l.getMinute());
        }

        ClockPresenter.userTime = ClockPresenter.userTime.withSecond(userTimeSeconds.getSecond()).withMinute(userTimeMinutes.getMinute());

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
        f.set(false);
    }

    public void setUserTimeSeconds(int second) {
        this.userTimeSeconds = userTimeSeconds.withSecond(second);
    }

    public void setUserTimeMinutes(int minute) {
        this.userTimeMinutes = userTimeMinutes.withMinute(minute);
    }

    private LocalTime clockTick(LocalTime current, Function<LocalTime, LocalTime> tick) {
        return tick.apply(current);
    }
}
