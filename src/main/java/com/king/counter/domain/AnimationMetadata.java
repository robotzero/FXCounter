package com.king.counter.domain;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public class AnimationMetadata {

    private final Rectangle rectangle;

    public AnimationMetadata(final Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Point2D getFrom() {
        if (rectangle.getTranslateY() == 0) {
            return new Point2D(rectangle.getTranslateX(), 240);
        }
        return new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY());
    }

    public Point2D getTo() {
        if (rectangle.getTranslateY() == 0) {
            return new Point2D(rectangle.getTranslateX(), 180);
        }
        return new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY() - 60);
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }
}