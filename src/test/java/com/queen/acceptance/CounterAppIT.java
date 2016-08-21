package com.queen.acceptance;

import com.airhacks.afterburner.injection.Injector;
import com.queen.acceptance.di.TestSpringApplicationConfiguration;
import com.queen.counter.clock.ClockView;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testfx.framework.junit.ApplicationTest;

import java.time.LocalTime;

@RunWith(DataProviderRunner.class)
public class CounterAppIT extends ApplicationTest {

    private final ApplicationContext injector = new AnnotationConfigApplicationContext(TestSpringApplicationConfiguration.class);
    final static int TIME_WAIT = 650;
    final static int TOP_NODE_LOCATION = 0;
    final static int BOTTOM_NODE_LOCATION = 180;
    final static LocalTime DEFAULT_CLOCK_STATE = LocalTime.of(0, 16, 12);

    protected <T> T getBean(Class<T> c) {
        return injector.getBean(c);
    }

    @Override
    public void start(Stage stage) throws Exception {
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
        Scene scene = new Scene(clockView.getView(), 800, 600);

        stage.setX(2000);
        stage.setY(100);
        stage.setScene(scene);
        stage.show();
    }
}
