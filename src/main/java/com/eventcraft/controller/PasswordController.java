package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.service.UserService;
import com.eventcraft.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class PasswordController {
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button changePasswordButton;

    private UserService userService = new UserService();
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void changePassword(ActionEvent event) {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("All fields are required.", Alert.AlertType.WARNING);
            return;
        }

        if (!userService.verifyPassword(user, currentPassword)) {
            showAlert("Current password is incorrect.", Alert.AlertType.ERROR);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("New password and confirmation do not match.", Alert.AlertType.ERROR);
            return;
        }

        if (newPassword.length() < 8) {
            showAlert("New password must be at least 8 characters long.", Alert.AlertType.WARNING);
            return;
        }

        boolean isUpdated = userService.changeUserPassword(user, newPassword);
        if (isUpdated) {
            showAlert("Password changed successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Failed to change password. Try again later.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
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
