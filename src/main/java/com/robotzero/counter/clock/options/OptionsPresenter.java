package com.robotzero.counter.clock.options;

import com.robotzero.counter.service.StageController;
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


        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);
        optionClicks.subscribe(click -> stageController.setView());
    }
}
