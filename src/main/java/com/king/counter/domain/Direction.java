package com.king.counter.domain;

public enum Direction {
    UP(0, 0), DOWN(0, 0), LEFT(0, 0), RIGHT(0, 0), SWITCHUP(0, 0), SWITCHDOWN(0, 0), STARTUP(0, 0), STARTDOWN(0, 0);

    private final int x;

    private final int y;

    Direction(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }
}