package com.robotzero.acceptance.helpers;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class NodeFinder {

  public Supplier<List<Node>> getRectangles(StackPane stackPane) {
    return () ->
      stackPane
        .getChildren()
        .stream()
        .filter(n -> n.getClass().equals(VBox.class))
        //        .map(v -> ((VBox) v).getChildren())
        //        .flatMap(Collection::stream)
        //        .filter(s -> s.getClass().equals(StackPane.class))
        //        .map(sl -> ((StackPane) sl).getChildren())
        //        .flatMap(Collection::stream)
        //        .filter(r -> r.getClass().equals(Rectangle.class))
        .collect(Collectors.toList());
  }

  public Supplier<List<Node>> getLabels(StackPane stackPane) {
    return () ->
      stackPane
        .getChildren()
        .stream()
        .filter(n -> !n.getClass().equals(Rectangle.class))
        .map(v -> ((VBox) v).getChildren())
        .flatMap(Collection::stream)
        .filter(r -> r.getClass().equals(Text.class))
        .collect(Collectors.toList());
  }
}
