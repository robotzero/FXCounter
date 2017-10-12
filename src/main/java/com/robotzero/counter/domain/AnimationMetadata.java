package com.robotzero.counter.domain;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public class AnimationMetadata {

    private final Rectangle rectangle;

    public AnimationMetadata(final Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Point2D getFrom(double delta) {
        if (delta == 0 || delta < 0) {
            if (rectangle.getTranslateY() == 0) {
                return new Point2D(rectangle.getTranslateX(), 240);
            }
            return new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY());
        }

        if (rectangle.getTranslateY() == 240) {
            return new Point2D(rectangle.getTranslateX(), 0);
        }

        return new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY());
    }

    public Point2D getTo(double delta) {
        if (delta == 0 || delta < 0) {
            if (rectangle.getTranslateY() == 0) {
                return new Point2D(rectangle.getTranslateX(), 180);
            }
            return new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY() - 60);
        }

        if (rectangle.getTranslateY() == 240) {
            return new Point2D(rectangle.getTranslateX(), 60);
        }

        return new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY() + 60);
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }
}