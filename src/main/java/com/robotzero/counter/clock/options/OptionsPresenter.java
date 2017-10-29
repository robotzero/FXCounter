package com.robotzero.counter.clock.options;

import com.robotzero.counter.service.StageController;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.springframework.beans.factory.annotation.Autowired;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class OptionsPresenter implements Initializable {

    @FXML
    Label optionsLabel;

    @FXML
    GridPane rootNode;

    @Autowired
    StageController stageController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TranslateTransition a = new TranslateTransition();
        TranslateTransition b = new TranslateTransition();

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        rootNode.getChildren().add(vbox);

        Tile timeTile1 = TileBuilder.create()
            .prefSize(60, 60)
            .skinType(Tile.SkinType.CUSTOM)
            .title("Time Tile")
            .text("Whatever text")
            .duration(LocalTime.of(1, 22))
            .description("Average reply time")
            .textVisible(true)
            .build();

        Tile timeTile2 = TileBuilder.create()
            .prefSize(60, 60)
            .skinType(Tile.SkinType.CUSTOM)
            .title("Time Tile")
            .text("Whatever text")
            .duration(LocalTime.of(1, 22))
            .description("Average reply time")
            .textVisible(true)
            .build();
        
        vbox.getChildren().addAll(timeTile1, timeTile2);

        a.setNode(timeTile1);
        b.setNode(timeTile2);

        a.setFromY(timeTile1.getTranslateX());
        a.setToY(170);

        b.setFromY(timeTile2.getTranslateX());
        b.setToY(300);

        a.setDuration(Duration.millis(10000));
        b.setDuration(Duration.millis(10000));

        a.setCycleCount(10);
        b.setCycleCount(10);
        a.play();
        b.play();
        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);
        optionClicks.subscribe(click -> stageController.setView());
    }
}
