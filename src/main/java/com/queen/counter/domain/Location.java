package com.queen.counter.domain;

import javafx.geometry.Point2D;

public class Location {

    private final Point2D current;

    public Location(Point2D current)
    {
        this.current = current;
    }

    public Location move(Direction direction)
    {
        Point2D point = new Point2D(this.current.getX() + direction.getxOffset(), this.current.getY() + direction.getyOffset());
        return new Location(point);
    }

    public Point2D getCurrent() {
        return this.current;
    }
}
