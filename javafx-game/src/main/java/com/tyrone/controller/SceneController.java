package com.tyrone.controller;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

public class SceneController {
    // Controller can be null if no data needs to be passed
    public static void switchScene(ActionEvent event, String fxmlPath, Consumer<Object> controllerSetup)
            throws IOException {

        FXMLLoader loader = new FXMLLoader(SceneController.class.getResource(fxmlPath));
        Parent root = loader.load();

        // If caller wants to pass data to controller
        if (controllerSetup != null) {
            Object controller = loader.getController();
            controllerSetup.accept(controller);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
