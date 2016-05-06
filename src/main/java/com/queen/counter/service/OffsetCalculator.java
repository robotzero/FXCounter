package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.domain.UIService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple3;

import javax.annotation.PostConstruct;

public class OffsetCalculator {

    private final UIService uiService;
    private final Animator animator;

    private BooleanProperty currentLabel               = new SimpleBooleanProperty(false);
    private BooleanProperty foundEdgeRectangle         = new SimpleBooleanProperty(false);
    private IntegerProperty offset                     = new SimpleIntegerProperty(1);
    private IntegerProperty currentDelta               = new SimpleIntegerProperty(0);
    private NumberBinding changeRectangle              = Bindings.createIntegerBinding(() -> currentDelta.getValue() > 0 ? 0 : 240, currentDelta);

    public OffsetCalculator(UIService uiService, Animator animator) {
        this.uiService = uiService;
        this.animator = animator;
    }

    @PostConstruct
    public void init() {
        EventStream labelStream = EventStreams.valuesOf(currentLabel);
        EventStream deltaStream = EventStreams.valuesOf(currentDelta);
        EventStream edgeRectangleStream = EventStreams.valuesOf(foundEdgeRectangle);

        EventStream<Tuple3<Integer, Boolean, Boolean>> combo = EventStreams.combine(deltaStream, labelStream, edgeRectangleStream);

        labelStream.subscribe(label -> {
            System.out.println(label);
            if ((Boolean)label) {
                animator.setRunning(true);
            } else {
                animator.setMinutesRunning(true);
            }
        });

        combo.map(change -> {
            Integer delta = change.get1();
            Boolean label = change.get2();
            Boolean edgeRectangle = change.get3();
            if ((delta < 0 && edgeRectangle) || (label && edgeRectangle)) {
                return 2;
            }
            return 1;
        }).feedTo(offset);
    }

    public int getCurrentOffset() {
        return this.offset.getValue();
    }

    public void setFoundEdgeRectangle() {
        this.uiService.getCurrentRectanglesStream()
                .get()
                .filter(r -> r.getTranslateY() == changeRectangle.getValue().intValue())
                .findAny()
                .ifPresent(r -> this.foundEdgeRectangle.set(true));
    }

    public void setCurrentDelta(double delta) {
        this.currentDelta.set((int)delta);
    }

    public void setCurrentLabel(boolean label) {
        this.currentLabel.set(label);
    }

    public void resetFoundRectangle() {
        this.foundEdgeRectangle.set(false);
    }
}
