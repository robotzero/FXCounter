package com.robotzero.acceptance;

import com.robotzero.acceptance.di.TestSpringApplicationConfiguration;
import com.robotzero.acceptance.helpers.NodeFinder;
import com.robotzero.counter.ClockApp;
import com.robotzero.counter.clock.ClockController;
import com.robotzero.counter.helper.ViewNodeHelper;
import com.robotzero.di.TimerConfiguration;
import java.time.LocalTime;
import java.util.UUID;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxWeaver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ConfigurableApplicationContext;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Init;
import org.testfx.framework.junit5.Start;

@SpringBootTest(
  classes = { TimerConfiguration.class, TestSpringApplicationConfiguration.class },
  webEnvironment = WebEnvironment.NONE
)
@ExtendWith(ApplicationExtension.class)
public class ClockFxTest {
  private ConfigurableApplicationContext applicationContext;
  private final NodeFinder nodeFinder = new NodeFinder();
  public static final int TIME_WAIT = 650;
  static final int TOP_NODE_LOCATION = 0;
  static final LocalTime DEFAULT_CLOCK_STATE = LocalTime.of(0, 16, 12);
  Stage stage;
  private Parent root;

  @Init
  public void init() {
    this.applicationContext =
      new SpringApplicationBuilder().sources(ClockApp.class, TestSpringApplicationConfiguration.class).run();
  }

  <T> T getBean(Class<T> c) {
    return applicationContext.getBean(c);
  }

  @Start
  public void start(Stage stage) {
    this.stage = stage;
    //    if (!stage.isShowing()) {
    FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
    Parent root = fxWeaver.loadView(ClockController.class);
    this.root = root;
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
        WindowEvent.fireEvent(
          gridPane,
          new Event("", root, new EventType<>("StageShow" + UUID.randomUUID().toString()))
        );
        stage.show();
      }
    );
  }

  //  }

  void restart() {
    if (stage.isShowing()) {
      Platform.runLater(
        () -> {
          stage.setTitle("TEST");
          stage.hide();
          WindowEvent.fireEvent(
            (GridPane) root,
            new Event("", root, new EventType<>("StageShow" + UUID.randomUUID().toString()))
          );
          stage.setTitle("Count Me Bubbles!");
          stage.show();
        }
      );
    }
  }

  public NodeFinder getNodeFinder() {
    return nodeFinder;
  }
}
