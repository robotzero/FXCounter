package com.queen.configuration;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SceneConfiguration {

    private IntegerProperty height = new SimpleIntegerProperty();
    private IntegerProperty width = new SimpleIntegerProperty();

    public IntegerProperty getHeightObject()
    {
        return this.height;
    }

    public IntegerProperty getWidthObject()
    {
        return this.width;
    }

    public int getInitWidth()
    {
        return 400;
    }

    public int getInitHeight()
    {
        return 600;
    }
}
