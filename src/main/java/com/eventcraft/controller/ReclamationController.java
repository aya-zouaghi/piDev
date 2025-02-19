package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.service.ReclamationService;
import com.eventcraft.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ReclamationController {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;

    private User user;
    private final ReclamationService reclamationService;

    public ReclamationController() {
        this.reclamationService = new ReclamationService();
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("User set in ReclamationController: " + user.getIdUser());
    }

    @FXML
    private void submitReclamation(ActionEvent event) {
        if (user == null) {
            showAlert("User not set. Please log in again.", Alert.AlertType.ERROR);
            return;
        }

        String titre = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = "Complaint";

        if (titre.isEmpty() || description.isEmpty()) {
            showAlert("Both title and description are required.", Alert.AlertType.WARNING);
            return;
        }

        boolean isSubmitted = reclamationService.submitReclamation(user, titre, description, type);
        if (isSubmitted) {
            showAlert("Reclamation submitted successfully.", Alert.AlertType.INFORMATION);
            clearFields();
        } else {
            showAlert("Failed to submit reclamation. Please try again later.", Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void handleEditProfileNavigation(ActionEvent event) {
        navigateTo("/view/edit_profile.fxml", event);
    }

    @FXML
    private void handlePasswordNavigation(ActionEvent event) {
        navigateTo("/view/Password.fxml", event);
    }

    @FXML
    private void handleProfileNavigation(ActionEvent event) {
        String fxmlFile = SessionManager.isAdmin() ? "/view/admin.fxml" : "/view/profile.fxml";
        navigateToWithUserData(fxmlFile, event);
    }

    @FXML
    private void handleViewReclamationNavigation(ActionEvent event) {
        navigateToWithUserData("/view/view_reclamation.fxml", event);
    }

    private void navigateTo(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(page));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToWithUserData(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();

            // Set user data in the new controller
            if (fxmlFile.equals("/view/view_reclamation.fxml")) {
                ViewReclamationController viewReclamationController = loader.getController();
                viewReclamationController.setUser(user);
            } else if (SessionManager.isAdmin()) {
                AdminController adminController = loader.getController();
                adminController.setUserData(user);
            } else if (SessionManager.isClient()) {
                ProfileController profileController = loader.getController();
                profileController.setUserData(user);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(page));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
