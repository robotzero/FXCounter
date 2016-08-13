package com.queen.acceptance;

import com.airhacks.afterburner.injection.Injector;
import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.counter.clock.ClockView;
import com.queen.counter.domain.ColumnType;
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

import java.util.*;
import java.util.stream.IntStream;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.queen.acceptance.CounterAppIT.ExpectedValues.expected;
import static com.queen.acceptance.CounterAppIT.Sequence.sequence;
import static com.queen.acceptance.CounterAppIT.Step.step;
import static java.util.Arrays.asList;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(DataProviderRunner.class)
public class CounterAppIT extends ApplicationTest {

    private final ApplicationContext injector = new AnnotationConfigApplicationContext(SpringApplicationConfiguration.class);
    private final static int TIME_WAIT = 650;
    private final static int TOP_NODE_LOCATION = 0;
    private final static int BOTTOM_NODE_LOCATION = 180;

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
    public static Object[][] basicScrollsSetup() {
        return new Object[][]{
            {sequence(
                expected(60, "14", 240, "11"),
                step(ColumnType.SECONDS, VerticalDirection.UP, 1)
            )},
            {sequence(
                    expected(60, "14", 240, "11"),
                    step(ColumnType.SECONDS, VerticalDirection.UP, 1)
            )}
        };
    }

                // DOWN
//                { 1, VerticalDirection.DOWN, "#seconds", 180, 0, "10", "13"},
//                { 2, VerticalDirection.DOWN, "#seconds", 120, 180, "10" , "9"},
//                { 3, VerticalDirection.DOWN, "#seconds", 60, 120, "10", "9" },
//                { 4, VerticalDirection.DOWN, "#seconds", 0, 60, "10", "9" },
//                { 5, VerticalDirection.DOWN, "#seconds", 180, 0, "6", "9" },
//                { 6, VerticalDirection.DOWN, "#seconds", 120, 180, "6", "5" },
//                { 7, VerticalDirection.DOWN, "#seconds", 60, 120, "6", "5" },
//                { 8, VerticalDirection.DOWN, "#seconds", 0, 60, "6", "5" },
//                { 9, VerticalDirection.DOWN, "#seconds", 180, 0, "2", "5" },
//                { 10, VerticalDirection.DOWN, "#seconds", 120, 180, "2", "1" },
//                // UP

//                { 2, VerticalDirection.UP, "#seconds", 120, 60, "14", "15" },
//                { 3, VerticalDirection.UP, "#seconds", 180, 120, "14", "15" },
//                { 4, VerticalDirection.UP, "#seconds", 240, 180, "14", "15" },
//                { 5, VerticalDirection.UP, "#seconds", 60, 240, "18", "15" },
//                { 6, VerticalDirection.UP, "#seconds", 120, 60, "18", "19" },
//                { 7, VerticalDirection.UP, "#seconds", 180, 120, "18", "19" },
//                { 8, VerticalDirection.UP, "#seconds", 240, 180, "18", "19" },
//                { 9, VerticalDirection.UP, "#seconds", 60, 240, "22", "19" },

    @DataProvider
    public static Object[][] basicSecondsMinutesScrollsSetupUpDown() {


        // Number of scrolls, direction, group, timewait, expected new translate, expected label
        return new Object[][] {
                { 1, VerticalDirection.UP, "#seconds", 650, 60, "14" },
                { 2, VerticalDirection.UP, "#seconds", 650, 120, "14" },
                { 3, VerticalDirection.UP, "#seconds", 650, 180, "14" },
                { 4, VerticalDirection.UP, "#minutes", 650, 240, "18" },
                { 5, VerticalDirection.UP, "#seconds", 650, 60, "18" },
                { 6, VerticalDirection.UP, "#minutes", 650, 120, "22" },
                { 7, VerticalDirection.UP, "#seconds", 650, 180, "18" },
                { 8, VerticalDirection.UP, "#seconds", 650, 240, "18" },
                { 9, VerticalDirection.UP, "#seconds", 650, 60, "22" },
        };
    }
    
    @Test
    @UseDataProvider("basicScrollsSetup")
    public void basic_scroll(Sequence sequence) {

            Group seconds = assertContext().getNodeFinder().lookup("#seconds").queryFirst();
            Group minutes = assertContext().getNodeFinder().lookup("#minutes").queryFirst();

            String topIdSeconds = seconds.getChildren().stream().filter(r -> r.getClass().equals(Rectangle.class)).filter(r -> r.getTranslateY() == TOP_NODE_LOCATION).findAny().get().getId();
            String bottomIdSeconds = seconds.getChildren().stream().filter(r -> r.getClass().equals(Rectangle.class)).filter(r -> r.getTranslateY() == BOTTOM_NODE_LOCATION).findAny().get().getId();
            sequence.steps.forEach(step -> {
                moveTo("#" + step.columnType.name().toLowerCase());
                IntStream.range(0, step.scrollsNumber).forEach(i -> {
                    scroll(step.scrollsNumber, step.direction);
                    try {
                        WaitFor.waitUntil(timeout(millis(TIME_WAIT)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

            });

            verifyThat("#seconds", (Group s) -> {
                Optional<Node> exTopRectangle = seconds.getChildren().stream().filter(rs -> rs.getClass().equals(Rectangle.class)).filter(rt -> rt.getId().equals(topIdSeconds)).findAny();
                Optional<Node> exBottomRectangle = seconds.getChildren().stream().filter(rs -> rs.getClass().equals(Rectangle.class)).filter(rt -> rt.getId().equals(bottomIdSeconds)).findAny();
                if (exTopRectangle.isPresent()) {
                    String label =  seconds.getChildren().stream().filter(rk -> rk.getClass().equals(Text.class)).filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                    return exTopRectangle.get().getTranslateY() == sequence.expectedValues.topPosition && label.equals(sequence.expectedValues.topLabel);
                }

                if (exBottomRectangle.isPresent()) {
                    String lb =  seconds.getChildren().stream().filter(rk -> rk.getClass().equals(Text.class)).filter(tr -> tr.getId().equals(bottomIdSeconds)).map(tt -> ((Text) tt).getText()).findAny().get();
                    return exBottomRectangle.get().getTranslateY() == sequence.expectedValues.bottomPosition && lb.equals(sequence.expectedValues.bottomLabel);
                }

                return false;
            });
    }

    // @TODO check minutes group as well.
    // @TODO check cases around 0 and 50
    // @TODO test ticking, including minutes are up when seconds are at 0
    // @TODO test that you can't move columns when time is ticking.

    static class Step {
        ColumnType columnType;
        VerticalDirection direction;
        Integer scrollsNumber;

        public static Step step(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber) {
            Step step = new Step();
            step.columnType = columnType;
            step.direction = direction;
            step.scrollsNumber = scrollsNumber;
            return step;
        }
    }

    static class Sequence {
        List<Step> steps = new ArrayList<>();
        ExpectedValues expectedValues;

        public static Sequence sequence(ExpectedValues expectedValues, Step ...step) {
            Sequence sequence = new Sequence();
            sequence.steps.addAll(asList(step));
            sequence.expectedValues = expectedValues;
            return sequence;
        }
    }

    static class ExpectedValues {
        String topLabel;
        String bottomLabel;
        Integer topPosition;
        Integer bottomPosition;

        public static ExpectedValues expected(Integer topPosition, String topLabel, Integer bottomPosition, String bottomLabel) {
            ExpectedValues exp = new ExpectedValues();
            exp.bottomLabel = bottomLabel;
            exp.topLabel = topLabel;
            exp.topPosition = topPosition;
            exp.bottomPosition = bottomPosition;

            return exp;
        }
    }
}
