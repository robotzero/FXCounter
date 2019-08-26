package com.robotzero.counter;

import com.airhacks.afterburner.injection.Injector;
import com.robotzero.counter.clock.ClockView;
import com.robotzero.counter.service.StageController;
import com.robotzero.di.TimerConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Counter extends Application {

    private final ApplicationContext injector = new AnnotationConfigApplicationContext(TimerConfiguration.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Injector.setInstanceSupplier(injector::getBean);
        Injector.setConfigurationSource(null);
        StageController stageController = injector.getBean(StageController.class);
        stageController.setStage(primaryStage);
        stageController.setView();
        stageController.afterPropertiesSet();
        Platform.runLater(() -> {
            primaryStage.setTitle("Count Me Bubbles!");
            primaryStage.setX(2000);
            primaryStage.setY(100);
            ClockView clockView = injector.getBean(ClockView.class);
            GridPane gridPane = (GridPane) clockView.getView();
            WindowEvent.fireEvent(gridPane, new Event("", gridPane, new EventType<>("StageShow")));
            primaryStage.show();
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
