package com.queen.counter.domain;

import javafx.geometry.Point2D;

public class Location {

    private final Point2D point;

    public Location(Point2D point)
    {
        this.point = point;
    }

    public Location move(Direction direction)
    {
        Point2D point = new Point2D(this.point.getX() + direction.getxOffset(), this.point.getY() + direction.getyOffset());
        return new Location(point);
    }
}
