package com.robotzero.counter;

import com.airhacks.afterburner.injection.Injector;
import com.robotzero.counter.clock.ClockPresenter;
import com.robotzero.counter.clock.ClockView;
import com.robotzero.counter.domain.Column;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.service.Populator;
import com.robotzero.counter.service.StageController;
import com.robotzero.di.TimerConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

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
        primaryStage.setTitle("Count Me Bubbles!");
        primaryStage.setX(2000);
        primaryStage.setY(100);
        Platform.runLater(() -> {
            Populator populator = injector.getBean(Populator.class);
            ClockView clockView = injector.getBean(ClockView.class);
            clockView.getViewAsync(gridPane -> {
                ClockPresenter clockPresenter= (ClockPresenter) clockView.getPresenter();
                Map<ColumnType, Column> timerColumns = populator.timerColumns((GridPane) gridPane);
                clockPresenter.setTimerColumns(timerColumns);
                primaryStage.show();
            });
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
