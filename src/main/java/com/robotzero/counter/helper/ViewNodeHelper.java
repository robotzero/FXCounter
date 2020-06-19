package com.robotzero.counter.helper;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public final class ViewNodeHelper {
  private static GridPane gridPane;

  public static void setGridPane(final GridPane gridPaneNode) {
    gridPane = gridPaneNode;
  }

  public static ObservableList<Node> getUIChildren() {
    return gridPane.getChildrenUnmodifiable();
  }
}
