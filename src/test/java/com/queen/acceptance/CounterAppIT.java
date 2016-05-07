package com.queen.acceptance;

import com.airhacks.afterburner.injection.Injector;
import com.queen.counter.clock.ClockView;
import com.queen.di.SpringApplicationConfiguration;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testfx.framework.junit.ApplicationTest;

public class CounterAppIT extends ApplicationTest {

    private final ApplicationContext injector = new AnnotationConfigApplicationContext(SpringApplicationConfiguration.class);

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
        Scene scene = new Scene(clockView.getView());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void scroll_works() {
        
        System.out.println("#group");
    }
}
