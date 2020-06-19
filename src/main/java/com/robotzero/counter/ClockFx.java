package com.robotzero.counter;

import com.robotzero.counter.clock.ClockController;
import com.robotzero.counter.helper.ViewNodeHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class ClockFx extends Application {
  private ConfigurableApplicationContext applicationContext;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void init() {
    String[] args = getParameters().getRaw().toArray(new String[0]);

    this.applicationContext = new SpringApplicationBuilder().sources(ClockApp.class).run(args);
  }

  @Override
  public void start(Stage stage) {
    FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
    Parent root = fxWeaver.loadView(ClockController.class);
    Scene scene = new Scene(root);
    Platform.runLater(
      () -> {
        stage.setTitle("Count Me Bubbles!");
        stage.setX(1000);
        stage.setY(100);
        stage.setWidth(400);
        stage.setHeight(600);
        stage.setScene(scene);
        GridPane gridPane = (GridPane) root;
        ViewNodeHelper.setGridPane(gridPane);
        WindowEvent.fireEvent(gridPane, new Event("", gridPane, new EventType<>("StageShow")));
        stage.show();
      }
    );
  }

  @Override
  public void stop() {
    this.applicationContext.close();
    Platform.exit();
  }
}
