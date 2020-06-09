package com.robotzero.counter.service;

import com.airhacks.afterburner.views.FXMLView;
import com.robotzero.configuration.SceneConfiguration;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageController {

    private Stage primaryStage;

    private SceneConfiguration sceneConfiguration;
    private FXMLView mainView;
    private FXMLView optionsView;
    private FXMLView currentView;
    private Scene clockScene;
    private Scene optionsScene;

    public StageController(SceneConfiguration sceneConfiguration, FXMLView ...views) {
        this.sceneConfiguration = sceneConfiguration;
        this.mainView = views[0];
        this.optionsView = views[0];
    }

    public void setView() {
        if (this.clockScene == null) {
            this.clockScene = new Scene(this.mainView.getView());
        }

//        if (this.optionsScene == null) {
//            this.optionsScene = new Scene(this.optionsView.getView());
//        }

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

    public void afterPropertiesSet() {
        mainView.getViewAsync(rootNode -> {
            sceneConfiguration.getHeightObject().bind(rootNode.getScene().heightProperty());
            sceneConfiguration.getWidthObject().bind(rootNode.getScene().widthProperty());
        });

        optionsView.getViewAsync(rootNode -> {
            sceneConfiguration.getHeightObject().bind(rootNode.getScene().heightProperty());
            sceneConfiguration.getWidthObject().bind(rootNode.getScene().widthProperty());
        });
    }
}
