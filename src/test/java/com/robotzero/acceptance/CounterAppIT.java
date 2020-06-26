package com.robotzero.acceptance;

import com.robotzero.acceptance.di.TestSpringApplicationConfiguration;
import com.robotzero.acceptance.helpers.NodeFinder;
import com.robotzero.counter.ClockApp;
import com.robotzero.counter.ClockFx;
import com.robotzero.counter.clock.ClockController;
import com.robotzero.counter.helper.ViewNodeHelper;
import com.robotzero.di.TimerConfiguration;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalTime;

@SpringBootTest(classes = {TimerConfiguration.class, TestSpringApplicationConfiguration.class}, webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = {TimerConfiguration.class, TestSpringApplicationConfiguration.class})
public class CounterAppIT {

//    private final ApplicationContext injector = new AnnotationConfigApplicationContext(TestSpringApplicationConfiguration.class);
    public final static int TIME_WAIT = 650;
    final static int TOP_NODE_LOCATION = 0;
    final static LocalTime DEFAULT_CLOCK_STATE = LocalTime.of(0, 16, 12);
    final NodeFinder nodeFinder = new NodeFinder();

  private ConfigurableApplicationContext applicationContext;

  <T> T getBean(Class<T> c) {
    return applicationContext.getBean(c);
  }

  public static void main(String[] args) throws Exception {
//    launch(ClockFx.class);
  }

//  @Override
  public void init() {
//    String[] args = getParameters().getRaw().toArray(new String[0]);

    this.applicationContext = new SpringApplicationBuilder().sources(ClockApp.class, TestSpringApplicationConfiguration.class).run();
  }

//  @Override
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

//  @Override
  public void stop() {
    this.applicationContext.close();
    Platform.exit();
  }

//    @Override
//    public void start(Stage stage) {
//      System.out.println("a");
////      FxWeaver fxWeaver = injector.getBean(FxWeaver.class);
////      Parent root = fxWeaver.loadView(ClockController.class);
////      Scene scene = new Scene(root);
////
////        stage.setX(800);
////        stage.setY(600);
////        stage.setScene(scene);
//////        WindowEvent.fireEvent(gridPane, new Event("", gridPane, new EventType<>("StageShow")));
////        stage.show();
//    }
}
