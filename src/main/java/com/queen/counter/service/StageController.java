package com.queen.counter.service;

import com.airhacks.afterburner.views.FXMLView;
import com.queen.configuration.SceneConfiguration;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

public class StageController {

    private Stage primaryStage;

    private SceneConfiguration sceneConfiguration;
    private Scene mainView;
    private Scene optionsView;
    private Scene currentView = null;

    public StageController(SceneConfiguration sceneConfiguration, Scene ...scenes) {
        this.mainView = scenes[0];
        this.optionsView = scenes[1];
        this.sceneConfiguration = sceneConfiguration;
    }

    @PostConstruct
    public void init() {
        sceneConfiguration.getHeightObject().bind(mainView.heightProperty());
        sceneConfiguration.getWidthObject().bind(mainView.widthProperty());
        sceneConfiguration.getHeightObject().bind(optionsView.heightProperty());
        sceneConfiguration.getWidthObject().bind(optionsView.widthProperty());
    }
    public void setView() {
        if (this.currentView != null && this.currentView.equals(mainView)) {
            this.currentView = this.optionsView;
        } else {
            this.currentView = this.mainView;
        }
        this.primaryStage.setScene(currentView);
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setHeight(sceneConfiguration.getInitHeight());
        this.primaryStage.setWidth(sceneConfiguration.getInitWidth());
    }
}
