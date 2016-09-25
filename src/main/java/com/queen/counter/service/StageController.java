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
    private Scene clockScene;
    private Scene optionsScene;

    public StageController(SceneConfiguration sceneConfiguration, FXMLView ...views) {
        this.sceneConfiguration = sceneConfiguration;
        this.mainView = views[0];
        this.optionsView = views[1];
    }

    @PostConstruct
    public void init() {
        mainView.getViewAsync(rootNode -> {
            sceneConfiguration.getHeightObject().bind(rootNode.getScene().heightProperty());
            sceneConfiguration.getWidthObject().bind(rootNode.getScene().widthProperty());
        });

        optionsView.getViewAsync(rootNode -> {
            sceneConfiguration.getHeightObject().bind(rootNode.getScene().heightProperty());
            sceneConfiguration.getWidthObject().bind(rootNode.getScene().widthProperty());
        });
    }

    public void setView() {
        if (this.clockScene == null) {
            this.clockScene = new Scene(this.mainView.getView());
        }

        if (this.optionsScene == null) {
            this.optionsScene = new Scene(this.optionsView.getView());
        }

        if (this.currentView != null && this.currentView.equals(mainView)) {
            this.currentView = this.optionsView;
            this.primaryStage.setScene(optionsScene);
        } else {
            this.currentView = this.mainView;
            this.primaryStage.setScene(clockScene);
        }
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setHeight(sceneConfiguration.getInitHeight());
        this.primaryStage.setWidth(sceneConfiguration.getInitWidth());
    }
}
