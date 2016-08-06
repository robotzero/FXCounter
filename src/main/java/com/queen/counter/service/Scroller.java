package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.AnimationMetadata;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.scene.text.Text;
import org.reactfx.EventSource;

import java.util.List;
import java.util.stream.Collectors;

public class Scroller {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;
    private final Clocks clocks;
    private final UIService uiService;
    private final EventSource eventSource;
    private final OffsetCalculator offsetCalculator;

    private BooleanProperty label = new SimpleBooleanProperty(false);

    private IntegerProperty delta = new SimpleIntegerProperty(0);
    private NumberBinding changeRectangle = Bindings.createIntegerBinding(() -> delta.getValue() > 0 ? 0 : 240, delta);
    private NumberBinding compareRectangle = Bindings.createIntegerBinding(() -> delta.getValue() < 0 ? 0 : 240, delta);
    private StringProperty src = new SimpleStringProperty();

    public Scroller(
            final Animator animator,
            final InMemoryCachedServiceLocator cache,
            final Clocks clocks,
            final UIService uiService,
            final EventSource eventSource,
            final OffsetCalculator offsetCalculator
    ) {
        this.animator = animator;
        this.cache = cache;
        this.clocks = clocks;
        this.uiService = uiService;
        this.eventSource = eventSource;
        this.offsetCalculator = offsetCalculator;
    }

    public void scroll(final String columnName, double deltaY) {

        delta.set((int)deltaY);
        src.set(columnName);
        this.uiService.setCurrentGroupName(columnName);
        this.uiService.getCurrentRectanglesStream()
                .get()
                .filter(r -> r.getTranslateY() == changeRectangle.getValue().intValue())
                .findAny()
                .ifPresent(r -> offsetCalculator.setFoundEndgeRectangle(true));

        this.label.set(columnName.equals("group"));

        if (label.get()) {
            animator.setRunning(true);
        } else {
            animator.setMinutesRunning(true);
        }

        //int timeShift = this.clocks.clockTick(columnName, deltaY, this.offsetCalculator.getCurrentOffset());

        List<AnimationMetadata> l = this.uiService.getCurrentRectanglesStream().get()
                .map(n -> (AnimationMetadata) cache.get(AnimationMetadata.class, n))
                .collect(Collectors.toList());


//        if (this.uiService.getEdgeRectangleId(compareRectangle.getValue().intValue()) != null) {
//            this.uiService.getCurrentLabelsStream().get().filter(lbl -> lbl.getId()
//                    .equals(this.uiService.getEdgeRectangleId(compareRectangle.getValue().intValue())))
//                    .findFirst()
//                    .ifPresent(lbl -> ((Text) lbl).setText(timeShift + ""));
//        }

        animator.animate(l, deltaY, cache);
        this.offsetCalculator.setFoundEndgeRectangle(false);
    }
}
