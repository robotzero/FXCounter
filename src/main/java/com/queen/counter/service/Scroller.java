package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.AnimationMetadata;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple3;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Scroller {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;
    private final Clocks clocks;
    private final UIService uiService;
    private final EventSource eventSource;

    private BooleanProperty label = new SimpleBooleanProperty(false);
    private BooleanProperty f = new SimpleBooleanProperty(false);

    private IntegerProperty offset = new SimpleIntegerProperty(1);

    private IntegerProperty delta = new SimpleIntegerProperty(0);
    private NumberBinding changeRectangle = Bindings.createIntegerBinding(() -> delta.getValue() > 0 ? 0 : 240, delta);
    private NumberBinding compareRectangle = Bindings.createIntegerBinding(() -> delta.getValue() < 0 ? 0 : 240, delta);
    private StringProperty src = new SimpleStringProperty();
    
    public Scroller(final Animator animator, final InMemoryCachedServiceLocator cache, final Clocks clocks, final UIService uiService, final EventSource eventSource) {
        this.animator = animator;
        this.cache = cache;
        this.clocks = clocks;
        this.uiService = uiService;
        this.eventSource = eventSource;

        EventStream lab = EventStreams.valuesOf(label);
        EventStream del = EventStreams.valuesOf(delta);
        EventStream ff = EventStreams.valuesOf(f);

        EventStream<Tuple3<Integer, Boolean, Boolean>> combo = EventStreams.combine(del, ff, lab);

        combo.map(change -> {
            Integer delta = change.get1();
            Boolean found = change.get2();
            Boolean labl = change.get3();
            if ((delta < 0 && found) || (labl && found)) {
                return 2;
            }
            return 1;
        }).feedTo(offset);
    }

    public void scroll(final String columnName, double deltaY) {

        delta.set((int)deltaY);
        src.set(columnName);
        this.uiService.setCurrentGroupName(columnName);
        this.uiService.getCurrentRectanglesStream()
                .get()
                .filter(r -> r.getTranslateY() == changeRectangle.getValue().intValue())
                .findAny()
                .ifPresent(r -> f.set(true));

        this.label.set(columnName.equals("group"));

        if (label.get()) {
            animator.setRunning(true);
        } else {
            animator.setMinutesRunning(true);
        }

        int timeShift = this.clocks.clockTick(columnName, deltaY, offset.getValue());

        List<AnimationMetadata> l = this.uiService.getCurrentRectanglesStream().get()
                .map(n -> (AnimationMetadata) cache.get(AnimationMetadata.class, n))
                .collect(Collectors.toList());


        if (this.uiService.getEdgeRectangleId(compareRectangle.getValue().intValue()) != null) {
            this.uiService.getCurrentLabelsStream().get().filter(lbl -> lbl.getId()
                    .equals(this.uiService.getEdgeRectangleId(compareRectangle.getValue().intValue())))
                    .findFirst()
                    .ifPresent(lbl -> ((Text) lbl).setText(timeShift + ""));
        }

        animator.animate(l, deltaY, cache);
        offset.set(1);
        f.set(false);
    }
}
