package com.queen.counter.domain;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UIService {

    //private final Supplier<Stream<?>> rectanglesSupplier;
    //private final ObservableList<Node> rectanglesSupplier;
    private Supplier<Stream<Group>> groups;

    public UIService() {

        //System.out.println(this.groups.map(Group::getChildren).filter(r -> r.getClass().equals(Rectangle.class)).collect(Collectors.toList()));
//        this.rectanglesSupplier =
//                () -> this.groups.map(r -> {
//
//                    return null;
//                });

//        this.rectanglesSupplier =
//                () -> Stream.of(group.getChildren(), minutesgroup.getChildren())
//                        .flatMap(Collection::stream)
//                        .filter(r -> r.getClass().equals(Rectangle.class));
    }

    public void setRectanglesGroups(Supplier<Stream<Group>> groups) {
        this.groups = groups;
    }

    public Stream<Group> getRectanglesGroups() {
        return this.groups.get();
    }

    public Stream<Rectangle> getRectangles(final String groupId, final Class nodeType) {
        return this.groups.get().filter(g -> g.getId().equals(groupId)).map(Group::getChildren).map(node -> {
            List<Rectangle> tmparr = new ArrayList<>();
            node.forEach(r -> {
               if (r.getClass().equals(nodeType)) {
                    tmparr.add((Rectangle) r);
               }
            });
            return tmparr;
        }).flatMap(Collection::stream);
    }
//    public Stream<Group> getRectanglesGroups() {
//        return this.groups;
//    }

//    public void getRectanglesStream() {
//        System.out.println(this.groups.map(Group::getChildren).filter(r -> r.getClass().equals(Rectangle.class)).collect(Collectors.toList()));
//        //return this.rectanglesSupplier;
//    }
}
