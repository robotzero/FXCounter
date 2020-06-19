package com.robotzero.counter.view;

import static java.util.stream.Collectors.collectingAndThen;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.helper.ViewFilterHelper;
import com.robotzero.counter.helper.ViewNodeHelper;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CellsFactory {

  public static Map<ColumnType, Column> build() {
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
          TranslateTransition translateTransition = new TranslateTransition();
          VBox vbox = (VBox) node;
          translateTransition.setNode(vbox);
          Cell cell = new Cell(
            Integer.parseInt(vbox.getId()),
            ((Text) vbox.getChildren().get(0)),
            translateTransition,
            new SimpleIntegerProperty(90)
          );
          ColumnType columnType = ColumnType.valueOf(node.getParent().getId().toUpperCase());
          return new CellWithColumnType(cell, columnType);
        }
      )
      .collect(
        Collectors.groupingBy(
          CellWithColumnType::getColumnType,
          collectingAndThen(
            Collectors.toMap(cellWithColumnType -> cellWithColumnType.getCell().getId(), CellWithColumnType::getCell),
            Column::new
          )
        )
      );
  }

  private static class CellWithColumnType {
    private final Cell cell;
    private final ColumnType columnType;

    public CellWithColumnType(Cell cell, ColumnType columnType) {
      this.cell = cell;
      this.columnType = columnType;
    }

    public Cell getCell() {
      return cell;
    }

    public ColumnType getColumnType() {
      return columnType;
    }
  }
}
