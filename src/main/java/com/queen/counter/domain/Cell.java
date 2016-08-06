package com.queen.counter.domain;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Cell {

    private Rectangle rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty greaterDelta = new SimpleBooleanProperty(false);
    private BooleanProperty hasEdgeRectangle = new SimpleBooleanProperty(false);
    private BooleanProperty hasChangeTextRecangle = new SimpleBooleanProperty(false);


    public Cell(Rectangle rectangle, Location location, Text label, TranslateTransition translateTransition) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.translateTransition.setOnFinished(event -> this.running.set(false));
        final NumberBinding RectangleYEdge = new When(greaterDelta).then(0).otherwise(240);
        final NumberBinding RectangleYText = new When(greaterDelta).then(240).otherwise(0);
        this.hasEdgeRectangle.bind(new When(rectangle.translateYProperty().isEqualTo(RectangleYEdge)).then(true).otherwise(false));
        this.hasChangeTextRecangle.bind(new When(rectangle.translateYProperty().isEqualTo(RectangleYText)).then(true).otherwise(false));
    }

    public void setUpTransition(double delta) {
        
        if (delta == 0 || delta < 0) {
            if (this.rectangle.getTranslateY() == 0) {
                translateTransition.setFromY(240);
                translateTransition.setToY(180);
            } else {
                translateTransition.setFromY(rectangle.getTranslateY());
                translateTransition.setToY(rectangle.getTranslateY() - 60);
            }
        } else {
            if (rectangle.getTranslateY() == 240) {
                translateTransition.setFromY(0);
                translateTransition.setToY(60);
            } else {
                translateTransition.setFromY(rectangle.getTranslateY());
                translateTransition.setToY(rectangle.getTranslateY() + 60);
            }
        }
    }

    public void animate() {
        translateTransition.play();
    }

    public boolean hasEdgeRectangle() {
        return this.hasEdgeRectangle.get();
    }

    public boolean hasChangeTextRectangle() {
        return this.hasChangeTextRecangle.get();
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
    }
}
