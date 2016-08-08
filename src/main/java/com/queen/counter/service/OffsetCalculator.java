package com.queen.counter.service;

import javafx.beans.property.*;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple2;

import javax.annotation.PostConstruct;
import java.util.Random;

public class OffsetCalculator {

    private BooleanProperty foundEndgeRectangle = new SimpleBooleanProperty(false);
    private IntegerProperty offset              = new SimpleIntegerProperty(1);
    private IntegerProperty currentDelta        = new SimpleIntegerProperty(0);


    @PostConstruct
    public void init() {
        EventStream deltaStream = EventStreams.valuesOf(currentDelta);
        EventStream edgeRectangleStream = EventStreams.valuesOf(foundEndgeRectangle);

        EventStream<Tuple2<Integer, Boolean>> combo = EventStreams.combine(deltaStream, edgeRectangleStream);
        Random random = new Random();
        combo.map(change -> {
            Integer delta = change.get1();
            Boolean edgeRectangle = change.get2();
//            System.out.println(edgeRectangle);
            if (delta < 0 && edgeRectangle) {
//                System.out.println("one");
                return 2;
            }

            if (delta > 0 && edgeRectangle) {
//                System.out.println("two");
                return 2;
            }
            return random.nextInt(20);
//            return 1;
        }).feedTo(offset);
    }

    public int getCurrentOffset() {
        return this.offset.getValue();
    }

    public void setFoundEndgeRectangle(boolean found) {
        this.foundEndgeRectangle.set(found);
    }

    public void setDelta(double delta) {
        this.currentDelta.set((int) delta);
    }

    public BooleanProperty getFoundEdgeRecangle() {
        return this.foundEndgeRectangle;
    }
}
