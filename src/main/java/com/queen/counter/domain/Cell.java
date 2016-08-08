package com.queen.counter.domain;

import javafx.animation.TranslateTransition;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple2;

public class Cell {

    private Rectangle rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty greaterDelta = new SimpleBooleanProperty(false);
    private BooleanProperty edgeRectangle = new SimpleBooleanProperty(false);
    private BooleanProperty hasTextRectangle = new SimpleBooleanProperty(false);
    private IntegerProperty currentDelta = new SimpleIntegerProperty(0);
    private EventStream delta = EventStreams.valuesOf(currentDelta);
    private EventStream changeText = EventStreams.invalidationsOf(this.hasTextRectangle).supply(this);
    private NumberBinding RectangleYEdge = new When(greaterDelta).then(0).otherwise(240);
    private NumberBinding RectangleYText = new When(greaterDelta.and(currentDelta.isNotEqualTo(0))).then(0).otherwise(240);

    public Cell(Rectangle rectangle, Location location, Text label, TranslateTransition translateTransition) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.translateTransition.setOnFinished(event -> this.running.set(false));
        this.edgeRectangle.bind(new When(rectangle.translateYProperty().isEqualTo(RectangleYEdge)).then(true).otherwise(false));
//        this.hasTextRectangle.bind(new When(rectangle.translateYProperty().isEqualTo(RectangleYText)).then(true).otherwise(false));
        this.hasTextRectangle.bind(new When(translateTransition.fromYProperty().isEqualTo(240).and(greaterDelta.not()).and(currentDelta.isEqualTo(0))).then(true).otherwise(false));

        EventStream translateY = EventStreams.valuesOf(rectangle.translateYProperty());

        EventStream<Tuple2<Integer, Double>> combo = EventStreams.combine(
                delta, translateY
        );

        combo.map(change -> {
            Integer currDelta = change.get1();
            Double transY = change.get2();

            if (currDelta == 0 || currDelta < 0) {
                if (transY == 0) {
                    return 240;
                } else {
                    return transY;
                }
            } else {
                if (transY == 240) {
                    return 0;
                } else {
                    return transY;
                }
            }
        }).feedTo(translateTransition.fromYProperty());

        combo.map(change -> {
            Integer currDelta = change.get1();
            Double transY = change.get2();

            if (currDelta == 0 || currDelta < 0) {
                if (transY == 0) {
                    return 180;
                } else {
                    return transY - 60;
                }
            } else {
                if (transY == 240) {
                    return 60;
                } else {
                    return transY + 60;
                }
            }
        }).feedTo(translateTransition.toYProperty());
    }


    public void animate() {
        translateTransition.play();
    }

    public BooleanProperty hasEdgeRectangle() {
        return this.edgeRectangle;
    }

    public BooleanProperty hasChangeTextRectangle() {
        return this.hasTextRectangle;
    }

    public void setLabel(String newLabel) {
        if (rectangle.getId().equals(label.getId())) {
            this.label.setText(newLabel);
        }
    }

    public BooleanProperty isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public void setDelta(double delta) {
        this.greaterDelta.set(delta > 0);
        this.currentDelta.set((int) delta);
    }
}
