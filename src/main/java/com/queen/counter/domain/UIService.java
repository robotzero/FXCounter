package com.queen.counter.domain;

import javafx.scene.Group;
import javafx.scene.Node;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class UIService {

    //private final Supplier<Stream<?>> rectanglesSupplier;
    //private final ObservableList<Node> rectanglesSupplier;
    private Supplier<Stream<Group>> groups;

    public UIService() {

//        this.rectanglesSupplier =
//                () -> Stream.of(group.getChildren(), minutesgroup.getChildren())
//                        .flatMap(Collection::stream)
//                        .filter(r -> r.getClass().equals(Rectangle.class));
    }

    public void setGroups(Supplier<Stream<Group>> groups) {
        this.groups = groups;
    }

    public Stream<Group> getRectanglesGroups() {
        return this.groups.get();
    }

    public Supplier<Stream<Node>> getStream(Predicate<Node> group, Predicate<Node> predicate) {
        return () -> groups.get().flatMap(g -> g.getChildren().stream()).filter(group.and(predicate));
    }
}
