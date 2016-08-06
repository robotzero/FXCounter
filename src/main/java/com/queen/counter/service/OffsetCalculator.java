package com.queen.counter.service;

import javafx.beans.property.*;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple2;

import javax.annotation.PostConstruct;

public class OffsetCalculator {

    private BooleanProperty foundEndgeRectangle = new SimpleBooleanProperty(false);
    private IntegerProperty offset              = new SimpleIntegerProperty(1);
    private IntegerProperty currentDelta        = new SimpleIntegerProperty(0);


    @PostConstruct
    public void init() {
        EventStream deltaStream = EventStreams.valuesOf(currentDelta);
        EventStream edgeRectangleStream = EventStreams.valuesOf(foundEndgeRectangle);

        EventStream<Tuple2<Integer, Boolean>> combo = EventStreams.combine(deltaStream, edgeRectangleStream);

        combo.map(change -> {
            Integer delta = change.get1();
            Boolean edgeRectangle = change.get2();

            if (delta < 0 && edgeRectangle) {
                return 2;
            }

            return 1;
        }).feedTo(offset);
    }

    public int getCurrentOffset() {
        return this.offset.getValue();
    }

    public void setFoundEndgeRectangle(boolean found) {
        this.foundEndgeRectangle.setValue(found);
    }

    public void setDelta(double delta) {
        this.currentDelta.set((int) delta);
    }

    public BooleanProperty getFoundEdgeRecangle() {
        return this.foundEndgeRectangle;
    }
}
