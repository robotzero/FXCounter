package com.robotzero.counter.clock.options;

import com.robotzero.counter.service.StageController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.springframework.beans.factory.annotation.Autowired;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsPresenter implements Initializable {

    @FXML
    Label optionsLabel;

    @Autowired
    StageController stageController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);

        optionClicks.subscribe(click -> stageController.setView());
    }
}
