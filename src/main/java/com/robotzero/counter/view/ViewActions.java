package com.robotzero.counter.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class ViewActions {

  public void disableButton(Node node) {
    if (node.getClass().equals(Button.class)) {
      node.disableProperty().set(true);
    }
  }

  public void disableScroll(Node node) {}

  public void changeLabel(Node node, String label) {
    if (node.getClass().equals(Text.class)) {
      ((Text) node).setText(label);
    }

    throw new UnsupportedOperationException("Unable to change text on " + Node.class);
  }
}
