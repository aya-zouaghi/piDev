package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private ImageView profileImage;

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField emailField;

    private User currentUser;

    @FXML
    public void initialize() {
        // Ensure only admins can access this page
        if (!SessionManager.isAdmin()) {
            handleUnauthorizedAccess();
            return;
        }

        disableFields(true);
        currentUser = SessionManager.getUser();
        populateFields();
    }

    public void setUserData(User user) {
        this.currentUser = user;
        populateFields();
    }

    private void populateFields() {
        if (currentUser != null) {
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            roleField.setText(currentUser.getRole());
            emailField.setText(currentUser.getEmail());
            updateUserNameLabel();
        }
    }

    private void updateUserNameLabel() {
        if (userNameLabel != null && currentUser != null) {
            userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
        } else {
            System.err.println("userNameLabel or currentUser is null! Check FXML file or user data.");
        }
    }

    private void disableFields(boolean disable) {
        nomField.setDisable(disable);
        prenomField.setDisable(disable);
        roleField.setDisable(disable);
        emailField.setDisable(disable);
    }

    @FXML
    private void handleEditProfileNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit_profile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            editProfileController.initialize(currentUser);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load edit profile view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleAdminDashboard(ActionEvent event) {
        if (SessionManager.isAdmin()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Failed to load admin dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            handleUnauthorizedAccess();
        }
    }


    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handlePassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Password.fxml"));
            Parent passwordPage = loader.load();

            // Pass user data to PasswordController
            PasswordController passwordController = loader.getController();
            passwordController.setUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(passwordPage);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load password view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();
        // Navigate to the login page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent loginPage = loader.load();
            Scene loginScene = new Scene(loginPage);
            // Load the CSS
            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            loginScene.getStylesheets().add(css);

            // Use a node that is guaranteed to be initialized
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to navigate to login page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleUnauthorizedAccess() {
        showAlert("Unauthorized access. You do not have permission to view this page.", Alert.AlertType.ERROR);
        // Optionally, navigate to an error page or logout
    }
}
