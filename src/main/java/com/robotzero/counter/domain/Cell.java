package com.robotzero.counter.domain;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.observers.JavaFxObserver;
import io.reactivex.subjects.Subject;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Binding;
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
import java.util.Optional;
import java.util.stream.IntStream;

public class Cell {

    private VBox rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private BooleanProperty isCellOnTop = new SimpleBooleanProperty(false);
    private BooleanProperty isCellLabelToChange = new SimpleBooleanProperty(false);
    private Binding<Integer> currentDelta;
    private IntegerProperty currentSize;
    private Subject<Integer> deltaStream;
    private IntegerProperty currentMultiplayer = new SimpleIntegerProperty(0);

    public Cell(
            VBox rectangle,
            Location location,
            Text label,
            TranslateTransition translateTransition,
            Optional<Subject<Integer>> deltaStream,
            IntegerProperty currentSize
    ) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.deltaStream = deltaStream.orElseThrow(() -> new RuntimeException("Delta streams not initialized."));
        this.currentSize = currentSize;
        this.currentMultiplayer.set(Integer.valueOf(rectangle.getId()) - 1);

        this.isCellOnTop.bind(new When(rectangle.translateYProperty().isEqualTo(0).or(rectangle.translateYProperty().lessThan(0))).then(true).otherwise(false));
        this.isCellLabelToChange.bind(new When(rectangle.translateYProperty().greaterThan(currentSize.multiply(3)).and(rectangle.translateYProperty().lessThan(currentSize.multiply(4))).and(currentDelta.lessThan(0))).then(true).otherwise(
                new When(rectangle.translateYProperty().greaterThan(0).and(rectangle.translateYProperty().lessThan(currentSize)).and(currentDelta.greaterThan(0))).then(true).otherwise(false)
        ));
//        EventStream<Number> currentYposition = EventStreams.valuesOf(rectangle.translateYProperty());
        Observable<Number> currentYposition = JavaFxObservable.valuesOf(rectangle.translateYProperty());

        EventStream<Change<Number>> currentCellSize = EventStreams.changesOf(currentSize);

        //@TODO remove that.
        currentDelta = JavaFxObserver.toBinding(deltaStream.get());
//        currentDelta.bind(deltaStream.get()..toBinding(0));
        
        this.deltaStream.mergeWith(currentYposition);
        EventStream<Tuple2<Integer, Double>> combo = EventStreams.combine(
                this.deltaStream, currentYposition
        );

        currentCellSize.map(size -> size.getNewValue().intValue() * this.currentMultiplayer.get()).feedTo(rectangle.translateYProperty());

        combo.map(change -> {
            Integer currDelta = change.get1();
            Double transY = change.get2();
            return location.calculateFromY(currentSize, currDelta, transY);
        }).feedTo(translateTransition.fromYProperty());

        combo.map(change -> {
            Integer currDelta = change.get1();
            Double transY = change.get2();
            return location.calculateToY(currentSize, currDelta, transY);
        }).feedTo(translateTransition.toYProperty());
    }

    public void animate() {
        translateTransition.play();
        if (this.currentMultiplayer.get() == 3) {
            this.currentMultiplayer.set(0);
        } else {
            this.currentMultiplayer.set(this.currentMultiplayer.get() + 1);
        }
    }

    public void animateReset() {
        translateTransition.play();
    }

    public BooleanProperty isCellOnTop() {
        return this.isCellOnTop;
    }

    public BooleanProperty hasChangeTextRectangle() {
        return this.isCellLabelToChange;
    }

    public void setLabel(int newLabel) {
        if (label.getId().contains(rectangle.getId())) {
            this.label.setText(String.format("%02d", newLabel));
        }
    }

    public void setLabel(LocalTime clock, ColumnType columnType) {

        if (columnType.equals(ColumnType.SECONDS)) {
            if (currentMultiplayer.get() == 0) {
                label.setText(String.format("%02d", clock.plusSeconds(2).getSecond()));
            }
            if (currentMultiplayer.get() == 1) {
                label.setText(String.format("%02d", clock.plusSeconds(1).getSecond()));
            }

            if (currentMultiplayer.get() == 2) {
                label.setText(String.format("%02d", clock.getSecond()));
            }

            if (currentMultiplayer.get() == 3) {
                label.setText(String.format("%02d", clock.minusSeconds(1).getSecond()));
            }
        }

        if (columnType.equals(ColumnType.MINUTES)) {
            if (currentMultiplayer.get() == 0) {
                label.setText(String.format("%02d", clock.plusMinutes(2).getMinute()));
            }
            if (currentMultiplayer.get() == 1) {
                label.setText(String.format("%02d", clock.plusMinutes(1).getMinute()));
            }

            if (currentMultiplayer.get() == 2) {
                label.setText(String.format("%02d", clock.getMinute()));
            }

            if (currentMultiplayer.get() == 3) {
                label.setText(String.format("%02d", clock.minusMinutes(1).getMinute()));
            }
        }

        if (columnType.equals(ColumnType.HOURS)) {
            if (currentMultiplayer.get() == 0) {
                label.setText(String.format("%02d", clock.plusHours(2).getHour()));
            }
            if (currentMultiplayer.get() == 1) {
                label.setText(String.format("%02d", clock.plusHours(1).getHour()));
            }

            if (currentMultiplayer.get() == 2) {
                label.setText(String.format("%02d", clock.getHour()));
            }

            if (currentMultiplayer.get() == 3) {
                label.setText(String.format("%02d", clock.minusHours(1).getHour()));
            }
        }
    }

    public ReadOnlyObjectProperty<Animation.Status> isRunning() {
        return translateTransition.statusProperty();
    }

    public int getDelta() {
        return this.currentDelta.get();
    }

    public void resetMultiplayer(boolean isOnTop) {

        if (isOnTop) {
            IntStream.range(1, 5).forEach(i -> {
                if (translateTransition.fromYProperty().get() == currentSize.get() * i) {
                    this.currentMultiplayer.set(i - 1);
                }
            });
        } else {
            IntStream.range(1, 5).forEach(i -> {
                if (translateTransition.fromYProperty().get() == currentSize.get() * i) {
                    if (i == 4) {
                        this.currentMultiplayer.set(0);
                    } else {
                        this.currentMultiplayer.set(i);
                    }
                }
            });
        }
    }
}
