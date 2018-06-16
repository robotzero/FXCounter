package com.robotzero.counter.domain;

import io.reactivex.Single;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.stream.IntStream;

public class Cell {

    private VBox rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private BooleanProperty isCellOnTop = new SimpleBooleanProperty(false);
    private IntegerProperty currentSize;
    private IntegerProperty currentMultiplayer = new SimpleIntegerProperty(0);
    private ColumnType columnType;

    public Cell(
            VBox rectangle,
            Location location,
            Text label,
            TranslateTransition translateTransition,
            IntegerProperty currentSize,
            ColumnType columnType
    ) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.currentSize = currentSize;
        this.currentMultiplayer.set(0);
        this.isCellOnTop.bind(new When(rectangle.translateYProperty().isEqualTo(-90L).or(rectangle.translateYProperty().lessThan(-80L))).then(true).otherwise(false));
        this.columnType = columnType;
    }

    public void animate(Direction direction, Duration duration) {
        double fromY = location.calculateFromY(currentSize, direction.getDelta(), rectangle.getTranslateY());
        double toY = location.calculateToY(currentSize, direction.getDelta(), rectangle.getTranslateY());
        translateTransition.setDuration(duration);
        translateTransition.setFromY(fromY);
        translateTransition.setToY(toY);
        translateTransition.play();
        if (this.currentMultiplayer.get() == 3) {
            this.currentMultiplayer.set(0);
        } else {
            this.currentMultiplayer.set(this.currentMultiplayer.get() + 1);
        }
    }

    public Single<ChangeCell> getChangeCell() {
        if (rectangle.translateYProperty().get() == -90 || rectangle.translateYProperty().get() == 270) {
            return Single.just(new ChangeCell(this, rectangle.getTranslateY()));
        }

        return Single.never();
    }

    public void setLabel(int newLabel) {
        if (!this.label.textProperty().getValue().equals(String.format("%02d", newLabel))) {
            this.label.textProperty().setValue(String.format("%02d", newLabel));
        }
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

    public ColumnType getColumnType() {
        return columnType;
    }
}
