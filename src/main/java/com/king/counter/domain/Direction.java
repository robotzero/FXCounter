package com.king.counter.domain;

public enum Direction {
    UP(0, -60), DOWN(0, 60), SWITCHUP(0, -60), SWITCHDOWN(0, 60), STARTUP(0, -60), STARTDOWN(0, 60);

    private final int xOffset;

    private final int yOffset;

    Direction(int xOffset, int yOffset)
    {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public int getxOffset()
    {
        return this.xOffset;
    }

    public int getyOffset()
    {
        return this.yOffset;
    }
}