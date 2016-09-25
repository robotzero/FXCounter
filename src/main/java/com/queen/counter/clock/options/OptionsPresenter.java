package com.queen.counter.clock.options;

import com.queen.counter.service.StageController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsPresenter implements Initializable {

    @FXML
    Label optionsLabel;

    @Inject
    StageController stageController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);

        optionClicks.subscribe(click -> stageController.setView());
    }
}
