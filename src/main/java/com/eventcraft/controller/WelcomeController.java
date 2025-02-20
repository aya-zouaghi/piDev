package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        System.out.println("WelcomeController initialized");

        // Ensure only authenticated users can access this page
        if (SessionManager.getCurrentUser() == null) {
            handleUnauthorizedAccess();
            return;
        }

        currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            updateWelcomeLabel();
        } else {
            showAlert("User data is not available.", Alert.AlertType.ERROR);
        }
    }


    private void updateWelcomeLabel() {
        if (welcomeLabel != null && currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getNom() + " " + currentUser.getPrenom());
        } else {
            System.err.println("welcomeLabel or currentUser is null! Check FXML file or user data.");
        }
    }

    @FXML
    private void handleProfile() {
        navigateTo("/view/profile.fxml");
    }

    @FXML
    private void handleEvents() {
        navigateTo("/view/homeso.fxml");
    }

    @FXML
    private void handleOffers() {
        navigateTo("/view/HELLO.fxml");
    }

    @FXML
    private void handleSalle() {
        navigateTo("/view/rooms.fxml");
    }

    @FXML
    private void handleDecorations() {
        navigateTo("/view/AfficherDecoration.fxml");
    }

    @FXML
    private void handleForum() {
        navigateTo("/view/forum-list.fxml");
    }

    private void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Load the CSS
            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to navigate to the selected page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleUnauthorizedAccess() {
        showAlert("Unauthorized access. You do not have permission to view this page.", Alert.AlertType.ERROR);
        // Optionally, navigate to an error page or logout
    }
}
