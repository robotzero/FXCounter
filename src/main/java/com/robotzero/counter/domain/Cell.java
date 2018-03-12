package com.robotzero.counter.domain;

import io.reactivex.Observable;
import io.reactivex.subjects.Subject;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import java.time.LocalTime;
import java.util.stream.IntStream;

public class Cell {

    private VBox rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private BooleanProperty isCellOnTop = new SimpleBooleanProperty(false);
    private BooleanProperty isCellLabelToChange = new SimpleBooleanProperty(false);
    private IntegerProperty currentSize;
    private Subject<Direction> deltaStream;
    private IntegerProperty currentMultiplayer = new SimpleIntegerProperty(0);

    public Cell(
            VBox rectangle,
            Location location,
            Text label,
            TranslateTransition translateTransition,
            Subject<Direction> deltaStream,
            IntegerProperty currentSize
    ) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.deltaStream = deltaStream;
        this.currentSize = currentSize;
//        this.currentMultiplayer.set(Integer.valueOf(rectangle.getId()) - 1);
        this.currentMultiplayer.set(0);
        this.isCellOnTop.bind(new When(rectangle.translateYProperty().isEqualTo(0L).or(rectangle.translateYProperty().lessThan(0L))).then(true).otherwise(false));
        EventStream<Change<Number>> currentCellSize = EventStreams.changesOf(currentSize);

        currentCellSize.map(size -> size.getNewValue().intValue() * this.currentMultiplayer.get()).feedTo(rectangle.translateYProperty());
    }

    public void animate(Direction direction) {
        translateTransition.setFromY(location.calculateFromY(currentSize, direction.getDelta(), rectangle.getTranslateY()));
        translateTransition.setToY(location.calculateToY(currentSize, direction.getDelta(), rectangle.getTranslateY()));
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

    public Observable<Cell> hasChangeTextRectangle() {
        return deltaStream.startWith(Observable.just(Direction.DOWN)).switchMap(direction -> {
            if (direction.getDelta() < 0 && rectangle.translateYProperty().get() >= currentSize.multiply(3).get()) {
                return Observable.just(this);
            }
            return Observable.empty();
        });
    }

    public void setLabel(int newLabel) {
        this.label.textProperty().setValue(String.format("%02d", newLabel));
    }

    public void setLabel(LocalTime clock, ColumnType columnType) {
        if (columnType.equals(ColumnType.SECONDS)) {
            if (currentMultiplayer.get() == 0) {
                System.out.println("BLAH");
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
