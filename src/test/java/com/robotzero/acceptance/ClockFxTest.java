package com.robotzero.acceptance;

import static org.testfx.api.FxAssert.assertContext;

import com.robotzero.acceptance.di.TestSpringApplicationConfiguration;
import com.robotzero.counter.ClockApp;
import com.robotzero.counter.clock.ClockController;
import com.robotzero.counter.domain.clock.TimerRepository;
import com.robotzero.counter.helper.ViewNodeHelper;
import com.robotzero.di.TimerConfiguration;
import java.time.LocalTime;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxWeaver;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ConfigurableApplicationContext;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Init;
import org.testfx.framework.junit5.Start;

@SpringBootTest(classes = {TimerConfiguration.class, TestSpringApplicationConfiguration.class}, webEnvironment = WebEnvironment.NONE)
@ExtendWith(ApplicationExtension.class)
public class ClockFxTest {

  private ConfigurableApplicationContext applicationContext;
  private TimerRepository repository;

  @Init
  public void init() {
    this.applicationContext = new SpringApplicationBuilder().sources(ClockApp.class, TestSpringApplicationConfiguration.class).run();
    repository = this.applicationContext.getBean(TimerRepository.class);
    repository.create("start", LocalTime.of(0, 0, 0));
    repository.deleteAll();
  }

  @Before
  public void setUp() {

  }

  @Start
  private void start(Stage stage) {
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

  @Test
  public void contextLoads(FxRobot fxRobot) {
    StackPane seconds = assertContext().getNodeFinder().lookup("#seconds").query();
    StackPane minutes = assertContext().getNodeFinder().lookup("#minutes").query();

//    FxAssert.verifyThat(button, LabeledMatchers.hasText("click me!"));
  }
}
