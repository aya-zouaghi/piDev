package com.eventcraft.controller;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.Stack;

public abstract class BaseController {

    protected static Stack<Scene> sceneHistory = new Stack<>();

    protected void navigateBack(Node source) {
        if (!sceneHistory.isEmpty()) {
            Scene previousScene = sceneHistory.pop();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(previousScene);
        } else {
            showAlert("No previous scene to go back to.", Alert.AlertType.INFORMATION);
        }
    }

    protected void pushSceneToHistory(Scene currentScene) {
        sceneHistory.push(currentScene);
    }

    protected abstract void showAlert(String message, Alert.AlertType alertType);
}
