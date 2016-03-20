package com.king.counter.domain;

import javafx.geometry.Point2D;

public class Location {

    private final Point2D point;

    public Location(Point2D point)
    {
        this.point = point;
    }

    public Location move(Direction direction)
    {
        Point2D point = new Point2D(this.point.getX() + direction.getX(), this.point.getY() + direction.getY());
        return new Location(point);
    }
}
