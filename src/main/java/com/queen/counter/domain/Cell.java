package com.queen.counter.domain;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple2;

import java.time.LocalTime;

public class Cell {

    private Rectangle rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private BooleanProperty edgeTopRectangle = new SimpleBooleanProperty(false);
    private BooleanProperty hasTextRectangle = new SimpleBooleanProperty(false);
    private IntegerProperty currentDelta = new SimpleIntegerProperty(0);
    private EventStream delta = EventStreams.valuesOf(currentDelta);
    private EventSource deltaStream;

    public Cell(Rectangle rectangle, Location location, Text label, TranslateTransition translateTransition, EventSource<Integer> deltaStream) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.deltaStream = deltaStream;
        this.edgeTopRectangle.bind(new When(rectangle.translateYProperty().isEqualTo(0)).then(true).otherwise(false));
        this.hasTextRectangle.bind(new When(rectangle.translateYProperty().greaterThan(180).and(rectangle.translateYProperty().lessThan(240)).and(currentDelta.lessThan(0))).then(true).otherwise(
                new When(rectangle.translateYProperty().greaterThan(0).and(rectangle.translateYProperty().lessThan(60)).and(currentDelta.greaterThan(0))).then(true).otherwise(false)
        ));
        EventStream<Number> translateY = EventStreams.valuesOf(rectangle.translateYProperty());
        currentDelta.bind(deltaStream.toBinding(0));

        EventStream<Tuple2<Integer, Double>> combo = EventStreams.combine(
                this.deltaStream, translateY
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

    public void setLabel(boolean topEdgeExists, LocalTime clock, ColumnType columnType) {
        if (columnType.equals(ColumnType.SECONDS)) {
            if (topEdgeExists) {
                if (rectangle.translateYProperty().get() == 0) {
                    label.setText(Integer.toString(clock.plusSeconds(2).getSecond()));
                }

                if (rectangle.translateYProperty().get() == 60) {
                    label.setText(Integer.toString(clock.plusSeconds(1).getSecond()));
                }

                if (rectangle.translateYProperty().get() == 120) {
                    label.setText(Integer.toString(clock.getSecond()));
                }

                if (rectangle.translateYProperty().get() == 180) {
                    label.setText(Integer.toString(clock.minusSeconds(1).getSecond()));
                }
            }

            if (!topEdgeExists) {
                if (rectangle.translateYProperty().get() == 60) {
                    label.setText(Integer.toString(clock.plusSeconds(2).getSecond()));
                }

                if (rectangle.translateYProperty().get() == 120) {
                    label.setText(Integer.toString(clock.plusSeconds(1).getSecond()));
                }

                if (rectangle.translateYProperty().get() == 180) {
                    label.setText(Integer.toString(clock.getSecond()));
                }

                if (rectangle.translateYProperty().get() == 240) {
                    label.setText(Integer.toString(clock.minusSeconds(1).getSecond()));
                }
            }
        }

        if (columnType.equals(ColumnType.MINUTES)) {
            if (topEdgeExists) {
                if (rectangle.translateYProperty().get() == 0) {
                    label.setText(Integer.toString(clock.plusMinutes(2).getMinute()));
                }

                if (rectangle.translateYProperty().get() == 60) {
                    label.setText(Integer.toString(clock.plusMinutes(1).getMinute()));
                }

                if (rectangle.translateYProperty().get() == 120) {
                    label.setText(Integer.toString(clock.getMinute()));
                }

                if (rectangle.translateYProperty().get() == 180) {
                    label.setText(Integer.toString(clock.minusMinutes(1).getMinute()));
                }
            }

            if (!topEdgeExists) {
                if (rectangle.translateYProperty().get() == 60) {
                    label.setText(Integer.toString(clock.plusMinutes(2).getMinute()));
                }

                if (rectangle.translateYProperty().get() == 120) {
                    label.setText(Integer.toString(clock.plusMinutes(1).getMinute()));
                }

                if (rectangle.translateYProperty().get() == 180) {
                    label.setText(Integer.toString(clock.getMinute()));
                }

                if (rectangle.translateYProperty().get() == 240) {
                    label.setText(Integer.toString(clock.minusMinutes(1).getMinute()));
                }
            }
        }
    }

    public ReadOnlyObjectProperty<Animation.Status> isRunning() {
        return translateTransition.statusProperty();
    }

    public int getDelta() {
        return this.currentDelta.get();
    }
}
