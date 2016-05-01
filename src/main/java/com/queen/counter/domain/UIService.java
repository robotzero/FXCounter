package com.queen.counter.domain;

import javafx.scene.Group;
import java.util.stream.Stream;

public class UIService {

    private Stream<Group> groups;

    public void setRectanglesGroups(Stream<Group> groups) {
        this.groups = groups;
    }

    public Stream<Group> getRectanglesGroups() {
        return this.groups;
    }
}
