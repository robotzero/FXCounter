package com.queen.acceptance;

import com.airhacks.afterburner.injection.Injector;
import com.google.code.tempusfugit.temporal.Condition;
import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.counter.clock.ClockView;
import com.queen.di.SpringApplicationConfiguration;
import javafx.geometry.VerticalDirection;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

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
    public void scroll_dramatic_test() {

        Group gs = assertContext().getNodeFinder().lookup("#group").queryFirst();
        gs.getChildren().stream().filter(r -> r.getClass().equals(Rectangle.class)).collect(Collectors.toList());
        gs.getChildren().stream().filter(l -> l.getClass().equals(Text.class)).collect(Collectors.toList());

//        verifyThat("#group", (Group g) -> {
//            g.getChildren().forEach(r -> {
//                if (r.getClass().equals(Rectangle.class)) {
//                    System.out.printf("rect " + r.getTranslateY() + "\n");
//                }
//            });
//            return true;
//        });
        moveTo("#group");

        try {
            WaitFor.waitOrTimeout(new Condition() {
                @Override
                public boolean isSatisfied() {
                    scroll(VerticalDirection.DOWN);
//                    Group r = assertContext().getNodeFinder().lookup("#group").queryFirst();
//                    Rectangle r1 = (Rectangle) r.getChildren().stream().filter(rt -> rt.getClass().equals(Rectangle.class)).findAny().get();
                    return false;
                    //return false;
                }
            }, timeout(millis(600)));
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (TimeoutException e) {
            //e.printStackTrace();
        }

        verifyThat("#group", (Group g) -> {
            System.out.println("after");
            g.getChildren().forEach(r -> {
                if (r.getClass().equals(Rectangle.class)) {
                    System.out.printf("rect " + r.getTranslateY() + "\n");
                }
            });
            return true;
        });
    }
}
