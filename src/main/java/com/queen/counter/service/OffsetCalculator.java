package com.queen.counter.service;

import com.queen.counter.domain.UIService;
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

    private BooleanProperty currentLabel               = new SimpleBooleanProperty(false);
    private BooleanProperty foundEndgeRectangle = new SimpleBooleanProperty(false);
    private IntegerProperty offset              = new SimpleIntegerProperty(1);
    private IntegerProperty currentDelta               = new SimpleIntegerProperty(0);

    public OffsetCalculator(UIService uiService) {
        this.uiService = uiService;
    }

    @PostConstruct
    public void init() {
        EventStream labelStream = EventStreams.valuesOf(currentLabel);
        EventStream deltaStream = EventStreams.valuesOf(currentDelta);
        EventStream edgeRectangleStream = EventStreams.valuesOf(foundEndgeRectangle);

        EventStream<Tuple3<Integer, Boolean, Boolean>> combo = EventStreams.combine(deltaStream, labelStream, edgeRectangleStream);

        combo.map(change -> {
            Integer delta = change.get1();
            Boolean edgeRectangle = change.get2();
            Boolean label = change.get3();
            if ((delta < 0 && edgeRectangle) || (label && edgeRectangle)) {
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
}
