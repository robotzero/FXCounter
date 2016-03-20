package com.king.counter;

import com.airhacks.afterburner.injection.Injector;
import com.king.configuration.SceneConfiguration;
import com.king.counter.clock.ClockView;
import com.king.di.SpringApplicationConfiguration;
import javafx.application.Application;
import javafx.scene.Scene;
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

        ClockView clockView = new ClockView();

        Scene scene = new Scene(clockView.getView());

        SceneConfiguration sceneConfiguration = (SceneConfiguration) injector.getBean("sceneConfiguration");
        sceneConfiguration.getHeightObject().bind(scene.heightProperty());
        sceneConfiguration.getWidthObject().bind(scene.widthProperty());

        primaryStage.setTitle("Count Me Bubbles!");
        primaryStage.setHeight(sceneConfiguration.getInitHeight());
        primaryStage.setWidth(sceneConfiguration.getInitWidth());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
