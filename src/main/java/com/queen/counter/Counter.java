package com.queen.counter;

import com.airhacks.afterburner.injection.Injector;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.service.StageController;
import com.queen.di.SpringApplicationConfiguration;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Counter extends Application {

    private final ApplicationContext injector = new AnnotationConfigApplicationContext(SpringApplicationConfiguration.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Injector.setConfigurationSource(null);

        Injector.setInstanceSupplier(new Injector.InstanceProvider() {
            @Override
            public boolean isInjectionAware() {
                return true;
            }

            @Override
            public boolean isScopeAware() {
                return true;
            }

            @Override
            public Object instantiate(Class<?> c) {
                return injector.getBean(c);
            }
        });

        StageController stageController = (StageController) injector.getBean("stageController");
        stageController.setStage(primaryStage);
        stageController.setView();

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
