package com.robotzero.counter.helper;

import com.robotzero.counter.domain.ColumnType;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.scene.Node;

public final class ViewFilterHelper {
  public static final Function<Class, Predicate<Node>> nodeClassPredicateFactory = clazz -> {
    return node -> node.getClass().equals(clazz);
  };
  public static final Function<ColumnType, Predicate<Node>> nodeTypePredicateFactory =
    (
      columnType -> {
        return node -> node.getId().equals(columnType.name().toLowerCase());
      }
    );
}
