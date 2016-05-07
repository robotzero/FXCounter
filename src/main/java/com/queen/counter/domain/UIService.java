package com.queen.counter.domain;

import com.queen.counter.cache.InMemoryCachedServiceLocator;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UIService {

    private InMemoryCachedServiceLocator cache;

    private StringProperty currentGroupName = new SimpleStringProperty();
    private IntegerProperty delta = new SimpleIntegerProperty();
    private NumberBinding compareRectangle = Bindings.createIntegerBinding(() -> delta.getValue() < 0 ? 0 : 240, delta);

    private Supplier<Stream<Group>> groups;
    private Predicate<Node> rectanglePredicate = r -> r.getClass().equals(Rectangle.class);
    private Predicate<Node> labelPredicate = l -> l.getClass().equals(Text.class);
    private Binding stringBinding = Bindings.createStringBinding(
            () -> currentGroupName.getValue().equals("group") ? "seconds" : "minutes", currentGroupName
    );

    public UIService(InMemoryCachedServiceLocator cache) {
        this.cache = cache;
    }

    public void setGroups(Supplier<Stream<Group>> groups) {
        this.groups = groups;
    }

    public Stream<Group> getRectanglesGroups() {
        return this.groups.get();
    }

    public Supplier<Stream<Node>> getCurrentRectanglesStream() {
        return () ->groups.get()
                .flatMap(g -> g.getChildren().stream())
                .filter(g -> g.getId().contains(stringBinding.getValue().toString()))
                .filter(rectanglePredicate);

    }

    public String getEdgeRectangleId(double delta) {
        this.delta.set((int) delta);
        Optional<Node> optional = this.getCurrentRectanglesStream().get().filter(r -> r.getTranslateY() == compareRectangle.getValue().intValue()).findAny();
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

    public List<AnimationMetadata> getCurrentAnimations() {
        return getCurrentRectanglesStream().get()
                .map(r -> (AnimationMetadata) cache.get(AnimationMetadata.class, r))
                .collect(Collectors.toList());
    }

    public void updateLabelText(double deltaY, int timeShift) {
        if (getEdgeRectangleId(deltaY) != null) {
            getCurrentLabelsStream().get().filter(lbl -> lbl.getId()
                    .equals(getEdgeRectangleId(deltaY)))
                    .findFirst()
                    .ifPresent(lbl -> ((Text) lbl).setText(timeShift + ""));
        }
    }
}
