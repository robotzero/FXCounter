package com.queen.counter.domain;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Cell {

    private Rectangle rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private boolean running = false;

    public Cell(Rectangle rectangle, Location location, Text label, TranslateTransition translateTransition) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.translateTransition.setOnFinished(event -> this.running = false);
    }

    public void setUpTransition(double delta) {
        translateTransition.setInterpolator(Interpolator.EASE_IN);

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

    public boolean hasEdgeRectangle(double delta) {
        int translateY = delta > 0 ? 0 : 240;

        return rectangle.getTranslateY() == translateY;
    }

    public boolean hasChangeTextRectangle(double delta) {
        int translateY = delta < 0 ? 0 : 240;

        return rectangle.getTranslateY() == translateY;
    }

    public String getId() {
        return rectangle.getId();
    }

    public void setLabel(String newLabel) {
        if (rectangle.getId().equals(label.getId())) {
            this.label.setText(newLabel);
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public TranslateTransition getTranslateTransition() {
        return this.translateTransition;
    }
}
