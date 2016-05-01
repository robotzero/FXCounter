package com.queen.counter.service;

import com.airhacks.afterburner.views.FXMLView;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.LocalTime;
import java.util.Random;
import java.util.stream.IntStream;

public class Populator {

    private final int cellsize = 60;
    private final int blockCount = 4;
    private final UIService uiService;
    private final Clocks clocks;


    public Populator(final UIService uiService, final Clocks clocks) {
        this.uiService = uiService;
        this.clocks = clocks;
    }

    public void populate() {

        final Random random = new Random();

        this.uiService.getRectanglesGroups().forEach(g -> IntStream.range(0, blockCount).mapToObj(i -> {
                Rectangle rectangle = new Rectangle(cellsize, 0, cellsize, cellsize);
                rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
                rectangle.setStrokeType(StrokeType.INSIDE);
                rectangle.setStroke(Color.BLACK);
                rectangle.setTranslateY(cellsize * i);

                final Text t = new Text();

                String id = "";
                if (g.getId().equals("group")) {
                    t.setText(this.clocks.getMainClock().getSecond() - i + 2 + "");
                    id = (random.nextInt(100) + "seconds");
                }

                if (g.getId().equals("minutesgroup")) {
                    t.setText(this.clocks.getMainClock().getMinute() - i + 2 + "");
                    id = (random.nextInt(100) + "minutes");
                }

                t.setFont(Font.font(20));

                t.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));

                t.yProperty().bind(rectangle.translateYProperty().add(rectangle.heightProperty().divide(2)));
                t.setTextAlignment(TextAlignment.CENTER);
                t.setTextOrigin(VPos.CENTER);

                t.setId(id);
                rectangle.setId(id);

                g.getChildren().addAll(rectangle, t);
                return 0;
            }).count());
    }
}
