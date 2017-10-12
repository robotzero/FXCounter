package com.robotzero.acceptance;

import com.airhacks.afterburner.injection.Injector;
import com.robotzero.acceptance.di.TestSpringApplicationConfiguration;
import com.robotzero.acceptance.helpers.NodeFinder;
import com.robotzero.counter.clock.ClockView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testfx.framework.junit.ApplicationTest;

import java.time.LocalTime;

public class CounterAppIT extends ApplicationTest {

    private final ApplicationContext injector = new AnnotationConfigApplicationContext(TestSpringApplicationConfiguration.class);
    public final static int TIME_WAIT = 650;
    final static int TOP_NODE_LOCATION = 0;
    final static LocalTime DEFAULT_CLOCK_STATE = LocalTime.of(0, 16, 12);
    final NodeFinder nodeFinder = new NodeFinder();

    <T> T getBean(Class<T> c) {
        return injector.getBean(c);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Injector.setInstanceSupplier(injector::getBean);
        Injector.setConfigurationSource(null);
        ClockView clockView = new ClockView();
        Scene scene = new Scene(clockView.getView(), 800, 600);

        stage.setX(2000);
        stage.setY(100);
        stage.setScene(scene);
        stage.show();
    }
}
