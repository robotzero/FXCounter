package com.queen.counter.domain;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.reactfx.Change;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple2;

import java.time.LocalTime;

public class Cell {

    private VBox rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private BooleanProperty edgeTopRectangle = new SimpleBooleanProperty(false);
    private BooleanProperty hasTextRectangle = new SimpleBooleanProperty(false);
    private IntegerProperty currentDelta = new SimpleIntegerProperty(0);
    private IntegerProperty currentSize;
    private EventSource deltaStream;
    private IntegerProperty currentMultiplayer = new SimpleIntegerProperty(0);

    public Cell(
            VBox rectangle,
            Location location,
            Text label,
            TranslateTransition translateTransition,
            EventSource<Tuple2<Integer, ColumnType>> deltaStream,
            IntegerProperty currentSize
    ) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.deltaStream = deltaStream;
        this.currentSize = currentSize;
        this.currentMultiplayer.set(Integer.valueOf(rectangle.getId()));

        this.edgeTopRectangle.bind(new When(rectangle.translateYProperty().isEqualTo(0).or(rectangle.translateYProperty().lessThan(0))).then(true).otherwise(false));
        this.hasTextRectangle.bind(new When(rectangle.translateYProperty().greaterThan(currentSize.multiply(3)).and(rectangle.translateYProperty().lessThan(currentSize.multiply(4))).and(currentDelta.lessThan(0))).then(true).otherwise(
                new When(rectangle.translateYProperty().greaterThan(0).and(rectangle.translateYProperty().lessThan(currentSize)).and(currentDelta.greaterThan(0))).then(true).otherwise(false)
        ));
        EventStream<Number> translateY = EventStreams.valuesOf(rectangle.translateYProperty());
        EventStream<Change<Number>> cellSize = EventStreams.changesOf(currentSize);
        currentDelta.bind(deltaStream.map(Tuple2::get1).toBinding(0));

        EventStream<Tuple2<Tuple2<Integer, ColumnType>, Double>> combo = EventStreams.combine(
                this.deltaStream, translateY
        );

        cellSize.map(size -> size.getNewValue().intValue() * this.currentMultiplayer.get()).feedTo(rectangle.translateYProperty());

        combo.map(change -> {
            Integer currDelta = change.get1().get1();
            Double transY = change.get2();

            if (currDelta == 0 || currDelta < 0) {
                if (transY == 0 || transY < 0) {
                    return currentSize.multiply(4).get();
                } else {
//                    return transY;
                    return 0;
                }
            } else {
                if (transY == currentSize.multiply(4).get()) {
                    return 0;
                } else {
                    return transY;
                }
            }
        }).feedTo(translateTransition.fromYProperty());

        combo.map(change -> {
            Integer currDelta = change.get1().get1();
            Double transY = change.get2();

            if (currDelta == 0 || currDelta < 0) {
                if (transY == 0 || transY < 0) {
                    return currentSize.multiply(3).get();
                } else {
                    return transY - currentSize.get();
                }
            } else {
                if (transY == currentSize.multiply(4).get()) {
                    return currentSize.get();
                } else {
                    return transY + currentSize.get();
                }
            }
        }).feedTo(translateTransition.toYProperty());
    }


    public void animate() {
        translateTransition.play();
        if (this.currentMultiplayer.get() == 4) {
            this.currentMultiplayer.set(1);
        } else {
            this.currentMultiplayer.set(this.currentMultiplayer.get() + 1);
        }
    }

    public BooleanProperty hasTopEdgeRectangle() {
        return this.edgeTopRectangle;
    }

    public BooleanProperty hasChangeTextRectangle() {
        return this.hasTextRectangle;
    }

    public void setLabel(String newLabel) {
        if (label.getId().contains(rectangle.getId())) {
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

        if (columnType.equals(ColumnType.HOURS)) {
            if (topEdgeExists) {
                if (rectangle.translateYProperty().get() == 0) {
                    label.setText(Integer.toString(clock.plusHours(2).getHour()));
                }

                if (rectangle.translateYProperty().get() == 60) {
                    label.setText(Integer.toString(clock.plusHours(1).getHour()));
                }

                if (rectangle.translateYProperty().get() == 120) {
                    label.setText(Integer.toString(clock.getHour()));
                }

                if (rectangle.translateYProperty().get() == 180) {
                    label.setText(Integer.toString(clock.minusHours(1).getHour()));
                }
            }

            if (!topEdgeExists) {
                if (rectangle.translateYProperty().get() == 60) {
                    label.setText(Integer.toString(clock.plusHours(2).getHour()));
                }

                if (rectangle.translateYProperty().get() == 120) {
                    label.setText(Integer.toString(clock.plusHours(1).getHour()));
                }

                if (rectangle.translateYProperty().get() == 180) {
                    label.setText(Integer.toString(clock.getHour()));
                }

                if (rectangle.translateYProperty().get() == 240) {
                    label.setText(Integer.toString(clock.minusHours(1).getHour()));
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
