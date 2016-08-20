package com.queen.counter.domain;

import javafx.animation.TranslateTransition;
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
    private BooleanProperty resetting = new SimpleBooleanProperty(false);
    private BooleanProperty greaterDelta = new SimpleBooleanProperty(false);
    private BooleanProperty edgeTopRectangle = new SimpleBooleanProperty(false);
    private BooleanProperty hasTextRectangle = new SimpleBooleanProperty(false);
    private IntegerProperty currentDelta = new SimpleIntegerProperty(0);
    private EventStream delta = EventStreams.valuesOf(currentDelta);

    public Cell(Rectangle rectangle, Location location, Text label, TranslateTransition translateTransition) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.translateTransition.setOnFinished(event -> {
            this.running.set(false);
            this.resetting.set(false);
        });
        this.edgeTopRectangle.bind(new When(rectangle.translateYProperty().isEqualTo(0)).then(true).otherwise(false));
        this.hasTextRectangle.bind(new When(rectangle.translateYProperty().greaterThan(180).and(rectangle.translateYProperty().lessThan(240)).and(currentDelta.lessThan(0))).then(true).otherwise(
                new When(rectangle.translateYProperty().greaterThan(0).and(rectangle.translateYProperty().lessThan(60)).and(currentDelta.greaterThan(0))).then(true).otherwise(false)
        ));
        EventStream<Number> translateY = EventStreams.valuesOf(rectangle.translateYProperty());

        translateY.suppressWhen(resetting.not()).map(translate -> {
            if (translate.intValue() == 0) {
                return Integer.toString(2);
            }

            if (translate.intValue() == 60) {
                return Integer.toString(1);
            }

            if (translate.intValue() == 120) {
                return Integer.toString(0);
            }

            if (translate.intValue() == 180) {
                return Integer.toString(59);
            }

            return "";
        }).feedTo(label.textProperty());

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

    public BooleanProperty hasTopEdgeRectangle() {
        return this.edgeTopRectangle;
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

    public void setResetting(boolean reset) {
        this.resetting.set(reset);
    }

    public BooleanProperty isDuringReset() {
        return this.resetting;
    }
}
