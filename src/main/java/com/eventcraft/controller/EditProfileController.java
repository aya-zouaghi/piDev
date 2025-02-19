package com.eventcraft.controller;

import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;
import com.eventcraft.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EditProfileController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField roleField;
    @FXML
    private TextField emailField;

    private User user;

    // Method to initialize the controller with user data
    public void initialize(User user) {
        if (user != null) {
            this.user = user;
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            roleField.setText(user.getRole());
            emailField.setText(user.getEmail());
        } else {
            showAlert("User data is not available.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to handle the save button click event
    @FXML
    private void saveProfile() {
        if (user != null) {
            try {
                // Retrieve updated data from text fields
                user.setNom(nomField.getText());
                user.setPrenom(prenomField.getText());
                user.setRole(roleField.getText());
                user.setEmail(emailField.getText());

                // Attempt to update the user in the database
                boolean success = UserDAO.updateUser(user);

                if (success) {
                    // Show a success message
                    showAlert("Profile updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    // Show a failure message
                    showAlert("Failed to update profile. Please try again.", Alert.AlertType.ERROR);
                }

            } catch (Exception e) {
                // Show an error message if an exception occurs
                showAlert("An error occurred while updating the profile: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleProfileNavigation(ActionEvent event) {
        try {
            String fxmlFile;
            if (SessionManager.isAdmin()) {
                fxmlFile = "/view/admin.fxml";
            } else if (SessionManager.isClient()) {
                fxmlFile = "/view/profile.fxml";
            } else {
                showAlert("Unauthorized access. You do not have permission to view this page.", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent profilePage = loader.load();

            // Get the appropriate controller and set the user data
            if (SessionManager.isAdmin()) {
                AdminController adminController = loader.getController();
                adminController.setUserData(user);
            } else if (SessionManager.isClient()) {
                ProfileController profileController = loader.getController();
                profileController.setUserData(user);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(profilePage);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load profile view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
