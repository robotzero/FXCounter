package com.queen.counter.service;

import com.airhacks.afterburner.views.FXMLView;
import com.queen.configuration.SceneConfiguration;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

public class StageController {

    private Stage primaryStage;

    private SceneConfiguration sceneConfiguration;
    private FXMLView mainView;
    private FXMLView optionsView;
    private FXMLView currentView = null;

    public StageController(SceneConfiguration sceneConfiguration, FXMLView ...views) {
        this.sceneConfiguration = sceneConfiguration;
        this.mainView = views[0];
        this.optionsView = views[1];
    }

    @PostConstruct
    public void init() {
//        sceneConfiguration.getHeightObject().bind(mainView.getView().getScene().heightProperty());
//        sceneConfiguration.getWidthObject().bind(mainView.getView().getScene().widthProperty());
//        sceneConfiguration.getHeightObject().bind(optionsView.getView().getScene().heightProperty());
//        sceneConfiguration.getWidthObject().bind(optionsView.getView().getScene().widthProperty());
    }
    public void setView() {
        if (this.currentView != null && this.currentView.equals(mainView)) {
            this.currentView = this.optionsView;
        } else {
            this.currentView = this.mainView;
        }
        this.primaryStage.setScene(new Scene(currentView.getView()));
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setHeight(sceneConfiguration.getInitHeight());
        this.primaryStage.setWidth(sceneConfiguration.getInitWidth());
    }
}
