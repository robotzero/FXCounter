package com.queen.counter.domain;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Cell {

    private Rectangle rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;

    public Cell(Rectangle rectangle, Location location, Text label, TranslateTransition translateTransition) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
    }

//    public void setUpTransition(double delta) {
//        if (delta == 0 || delta < 0) {
//            if (rectangle.getTranslateY() == 0) {
//                translateTransition.re new Point2D(rectangle.getTranslateX(), 240);
//            }
//            return new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY());
//        }
//
//        if (rectangle.getTranslateY() == 240) {
//            return new Point2D(rectangle.getTranslateX(), 0);
//        }
//
//        return new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY());
//    }
}
