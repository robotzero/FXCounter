package com.queen.counter.domain;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class UIService {

    private StringProperty currentGroupName = new SimpleStringProperty();

    private Supplier<Stream<Group>> groups;
    private Predicate<Node> rectanglePredicate = r -> r.getClass().equals(Rectangle.class);
    private Predicate<Node> labelPredicate = l -> l.getClass().equals(Text.class);
    private Binding stringBinding = Bindings.createStringBinding(
            () -> currentGroupName.getValue().equals("group") ? "seconds" : "minutes", currentGroupName
    );


    public void setGroups(Supplier<Stream<Group>> groups) {
        this.groups = groups;
    }

    public Stream<Group> getRectanglesGroups() {
        return this.groups.get();
    }

    public Supplier<Stream<Node>> getStream(Predicate<Node> group, Predicate<Node> predicate) {
        return () -> groups.get().flatMap(g -> g.getChildren().stream()).filter(group.and(predicate));
    }

    public Supplier<Stream<Node>> getCurrentRectanglesStream() {
        return () ->groups.get()
                .flatMap(g -> g.getChildren().stream())
                .filter(g -> g.getId().contains(stringBinding.getValue().toString()))
                .filter(rectanglePredicate);

    }

    public String getEdgeRectangleId(int translateY) {
        Optional<Node> optional = this.getCurrentRectanglesStream().get().filter(r -> r.getTranslateY() == translateY).findAny();
        if (optional.isPresent()) {
            return optional.get().getId();
        }

        return null;
    }

    public Supplier<Stream<Node>> getCurrentLabelsStream() {
        return () ->groups.get()
                .flatMap(g -> g.getChildren().stream())
                .filter(g -> g.getId().contains(stringBinding.getValue().toString()))
                .filter(labelPredicate);
    }

    public void setCurrentGroupName(String groupName) {
        this.currentGroupName.setValue(groupName);
    }
}
