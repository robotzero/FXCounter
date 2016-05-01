package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.AnimationMetadata;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple3;

import java.util.List;
import java.util.stream.Collectors;

public class Scroller {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;
    private final Clocks clocks;
    private final UIService uiService;
    private final EventSource eventSource;

    private BooleanProperty delta = new SimpleBooleanProperty(false);
    private BooleanProperty label = new SimpleBooleanProperty(false);
    private BooleanProperty f = new SimpleBooleanProperty(false);

    private IntegerProperty offset = new SimpleIntegerProperty(1);
    private IntegerProperty seconds = new SimpleIntegerProperty();
    private IntegerProperty minutes = new SimpleIntegerProperty();

    public Scroller(final Animator animator, final InMemoryCachedServiceLocator cache, final Clocks clocks, final UIService uiService, final EventSource eventSource) {
        this.animator = animator;
        this.cache = cache;
        this.clocks = clocks;
        this.uiService = uiService;
        this.eventSource = eventSource;

        EventStream lab = EventStreams.valuesOf(label);
        EventStream del = EventStreams.valuesOf(delta);
        EventStream ff = EventStreams.valuesOf(f);

        EventStream<Tuple3<Boolean, Boolean, Boolean>> combo = EventStreams.combine(del, ff, lab);
        //eventSource.subscribe(c -> this.scroll());

//        eventSource.subscribe(event -> {
//            System.out.println("MINUTES HAPPENED");
//           //this.scroll("minutesgroup", null, -40);
//        });

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

    public void scroll(final String columnName, final List<Text> labels, double deltaY) {

        final int found = deltaY > 0 ? 0 : 240;
        final int compare = deltaY < 0 ? 0 : 240;
        this.uiService.getRectangles(columnName, Rectangle.class).filter(r -> r.getTranslateY() == found).findAny().ifPresent(r -> f.set(true));
        delta.set(deltaY < 0);

        this.label.set(columnName.equals("group"));

        int offsetNumber = offset.getValue();

        if (label.get()) {
            animator.setRunning(true);
        } else {
            animator.setMinutesRunning(true);
        }

        int timeShift = this.clocks.clockTick(columnName, deltaY, offsetNumber);

        if (label.get()) {
            seconds.set(timeShift);
        } else {
            minutes.set(timeShift);
        }

        List<AnimationMetadata> l = this.uiService.getRectangles(columnName, Rectangle.class).map(n -> (AnimationMetadata) cache.get(AnimationMetadata.class, n)).collect(Collectors.toList());

        this.uiService.getRectangles(columnName, Rectangle.class).filter(r -> r.getTranslateY() == compare).findAny().ifPresent(r -> {
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
}
