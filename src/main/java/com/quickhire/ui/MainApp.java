package com.quickhire.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class MainApp {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // Call this from any controller to switch screens:
    // MainApp.switchScene("login.fxml");
    public static void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/quickhire/fxml/" + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            // Inside the catch block of switchScene:
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Use this when you need access to the controller after loading
    public static <T> T switchSceneAndGetController(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/quickhire/fxml/" + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}