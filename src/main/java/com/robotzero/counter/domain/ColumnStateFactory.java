package com.robotzero.counter.domain;

import static java.util.stream.Collectors.collectingAndThen;

import com.robotzero.counter.data.CellWithColumnType;
import com.robotzero.counter.helper.ViewFilterHelper;
import com.robotzero.counter.helper.ViewNodeHelper;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ColumnStateFactory {

  public static Map<ColumnType, ColumnState> build() {
    return ViewNodeHelper
      .getUIChildren()
      .filtered(
        ViewFilterHelper
          .nodeClassPredicateFactory.apply(StackPane.class)
          .and(
            ViewFilterHelper
              .nodeTypePredicateFactory.apply(ColumnType.SECONDS)
              .or(ViewFilterHelper.nodeTypePredicateFactory.apply(ColumnType.MINUTES))
              .or(ViewFilterHelper.nodeTypePredicateFactory.apply(ColumnType.HOURS))
          )
      )
      .stream()
      .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
      .filter(ViewFilterHelper.nodeClassPredicateFactory.apply(VBox.class))
      .map(
        node -> {
          VBox vBox = (VBox) node;
          final var cellState = new CellState(
            Integer.parseInt(vBox.getId()),
            new Location(vBox.getTranslateY(), vBox.getTranslateY()),
            new Location(vBox.getTranslateY(), vBox.getTranslateY()),
            DirectionType.VOID,
            DirectionType.VOID,
            CellStatePosition.NONCHANGABLE
          );
          ColumnType columnType = ColumnType.valueOf(node.getParent().getId().toUpperCase());
          return new CellWithColumnType<>(cellState, columnType);
        }
      )
      .collect(
        Collectors.groupingBy(
          CellWithColumnType::getColumnType,
          collectingAndThen(
            Collectors.toMap(cellWithColumnType -> cellWithColumnType.getCell().getId(), CellWithColumnType::getCell),
            ColumnState::new
          )
        )
      );
  }
}
