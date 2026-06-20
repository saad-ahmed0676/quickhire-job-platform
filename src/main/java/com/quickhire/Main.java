package com.quickhire;

import com.quickhire.ui.MainApp;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainApp.setPrimaryStage(stage);
        stage.setTitle("QuickHire");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        MainApp.switchScene("login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}