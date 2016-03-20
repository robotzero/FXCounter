package com.king.animator;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class Animator {

    private PointsRotator pointsRotator;

    public Animator(PointsRotator pointsRotator) {
        this.pointsRotator = pointsRotator;
    }

    public void animate(Group group)
    {
        List<Map<String, Point2D>> from = this.pointsRotator.rotate(group);

        group.getChildren().stream().map(rectangle -> {
            Map m = from.remove(0);
            Point2D pfrom = (Point2D) m.get("from");
            Point2D pto = (Point2D) m.get("to");
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(600), rectangle);
            translateTransition.setFromY(pfrom.getY());
            translateTransition.setToY(pto.getY());
            translateTransition.setInterpolator(Interpolator.EASE_OUT);

            return translateTransition;
        }).forEach(Animation::play);
    }
}
