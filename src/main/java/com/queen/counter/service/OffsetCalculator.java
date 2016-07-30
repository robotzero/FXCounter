package com.queen.counter.service;

import com.queen.counter.domain.UIService;
import javafx.beans.property.*;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple3;

import javax.annotation.PostConstruct;

public class OffsetCalculator {

    private final UIService uiService;

    //private BooleanProperty currentLabel               = new SimpleBooleanProperty(false);
    private StringProperty currentLabel         = new SimpleStringProperty("seconds");
    private BooleanProperty foundEndgeRectangle = new SimpleBooleanProperty(false);
    private IntegerProperty offset              = new SimpleIntegerProperty(1);
    private IntegerProperty currentDelta               = new SimpleIntegerProperty(0);

    public OffsetCalculator(UIService uiService) {
        this.uiService = uiService;
    }

    @PostConstruct
    public void init() {
        EventStream changeLabel = EventStreams.changesOf(currentLabel);
        //EventStream labelStream = EventStreams.valuesOf(currentLabel);
        EventStream deltaStream = EventStreams.valuesOf(currentDelta);
        EventStream edgeRectangleStream = EventStreams.valuesOf(foundEndgeRectangle);

        EventStream<Tuple3<Integer, Change, Boolean>> combo = EventStreams.combine(deltaStream, changeLabel, edgeRectangleStream);

        combo.map(change -> {
            Integer delta = change.get1();
            Change labelChanges = change.get2();
            Boolean edgeRectangle = change.get3();
            if (!labelChanges.getOldValue().equals(labelChanges.getNewValue())) {
                System.out.println("one");
                return 2;
            }

            if (delta < 0 && edgeRectangle) {
                System.out.println("two");
                return 2;
            }
            //Boolean edgeRectangle = change.get2();
//            Boolean label = change.get3();
//            if ((delta < 0 && edgeRectangle) || (label && edgeRectangle)) {
//                return 2;
//            }
            System.out.println("three");
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

    public void setLabel(String label) {
        this.currentLabel.set(label);
    }
}
