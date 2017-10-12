package com.robotzero.counter;

import com.airhacks.afterburner.injection.Injector;
import com.robotzero.counter.service.StageController;
import com.robotzero.di.SpringApplicationConfiguration;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Counter extends Application {

    private ApplicationContext injector = new AnnotationConfigApplicationContext(SpringApplicationConfiguration.class);

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
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
