package com.queen.counter.service;

import com.queen.counter.domain.*;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.reactfx.EventSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Populator {

    private final int cellsize = 60;
    private final int blockCount = 4;
    private final UIService uiService;
    private final Clocks clocks;
    private final EventSource[] clocksEvents;

    public Populator(final UIService uiService, final Clocks clocks, EventSource ...clocksEvents) {
        this.uiService = uiService;
        this.clocks = clocks;
        this.clocksEvents = clocksEvents;
    }

    public Column create(final String gid, Group group) {

        final Random random = new Random();
        List<Cell> list =  IntStream.range(0, blockCount).mapToObj(i -> {
            Rectangle rectangle = new Rectangle(cellsize, 0, cellsize, cellsize);
            rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStroke(Color.BLACK);
            rectangle.setTranslateY(cellsize * i);

            final Text label = new Text();

            String id = "";

            if (gid.equals("seconds")) {
                int second = this.clocks.getMainClock().getSecond() - i + 2;
                second = second == -1 ? 59 : second;
                label.setText(Integer.toString(second));
                id = i + "seconds";
            }

            if (gid.equals("minutes")) {
                int minute = this.clocks.getMainClock().getMinute() - i + 2;
                minute = minute == -1 ? 59 : minute;
                label.setText(Integer.toString(minute));
                id = i + "minutes";
            }

            label.setFont(Font.font(20));

            label.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));

            label.yProperty().bind(rectangle.translateYProperty().add(rectangle.heightProperty().divide(2)));
            label.setTextAlignment(TextAlignment.CENTER);
            label.setTextOrigin(VPos.CENTER);

            label.setId(id);
            rectangle.setId(id);

            group.getChildren().addAll(rectangle, label);

            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setNode(rectangle);
            return new Cell(rectangle, new Location(new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY())), label, translateTransition);
        }).collect(Collectors.toList());

        if (gid.equals("seconds")) {
            return new Column(list, clocks, ColumnType.SECONDS, clocksEvents[0]);
        }

        if (gid.equals("minutes")) {
            return new Column(list, clocks, ColumnType.MINUTES, clocksEvents[1]);
        }

        return new Column(list, clocks, ColumnType.HOURS, clocksEvents[2]);
    }
}
