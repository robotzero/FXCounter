package com.queen.acceptance;

import com.airhacks.afterburner.injection.Injector;
import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.counter.clock.ClockView;
import com.queen.di.SpringApplicationConfiguration;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javafx.geometry.VerticalDirection;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(DataProviderRunner.class)
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
        Scene scene = new Scene(clockView.getView(), 800, 600);

        stage.setScene(scene);
        stage.show();
    }

    @DataProvider
    public static Object[][] scrollsSetup() {

        return new Object[][] {
                { 1, VerticalDirection.DOWN, "#group", 650 },
        };
    }

    @Test
    @UseDataProvider("scrollsSetup")
    public void scroll_dramatic_test(int scrollsNumber, VerticalDirection direction, String node, int waitTime) {

        Group gs = assertContext().getNodeFinder().lookup(node).queryFirst();

        String id = gs.getChildren().stream().filter(r -> r.getClass().equals(Rectangle.class)).filter(r -> r.getTranslateY() == 0).findAny().get().getId();
        Text l = (Text) gs.getChildren().stream().filter(lbl -> lbl.getClass().equals(Text.class)).filter(lbl1 -> lbl1.getId().equals(id)).findAny().get();
        String t = l.getText();

        IntStream.range(0, scrollsNumber).forEach(i -> {
            moveTo(node);
            scroll(direction);
            try {
                WaitFor.waitUntil(timeout(millis(waitTime)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        verifyThat(node, (Group g) -> {
            Optional<Node> r = gs.getChildren().stream().filter(rs -> rs.getClass().equals(Rectangle.class)).filter(rt -> rt.getId().equals(id)).findAny();
            if (r.isPresent()) {
                String lb =  gs.getChildren().stream().filter(rk -> rk.getClass().equals(Text.class)).filter(tr -> tr.getId().equals(id)).map(tt -> ((Text) tt).getText()).findAny().get();
                int ints = new Integer(lb);
                int intb = new Integer(t);

                return r.get().getTranslateY() == 180 && ints == intb - 4;
            }
            return false;
        });
    }
}
