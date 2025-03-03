package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {

    @FXML
    private ImageView profileImage;

    @FXML
    private Label userNameLabel;

    @FXML
    private ListView<String> profileDetailsList;

    private User currentUser;

    @FXML
    public void initialize() {
        // Ensure only clients can access this page
        if (!SessionManager.isClient()) {
            handleUnauthorizedAccess();
            return;
        }

        currentUser = SessionManager.getUser();
        if (currentUser != null) {
            populateFields();
        } else {
            showAlert("User data is not available.", Alert.AlertType.ERROR);
        }
    }

    public void setUserData(User user) {
        this.currentUser = user;
        if (user != null) {
            populateFields();
        } else {
            showAlert("User data is not available.", Alert.AlertType.ERROR);
        }
    }

    private void populateFields() {
        if (currentUser != null) {
            ObservableList<String> profileDetails = FXCollections.observableArrayList(
                    "Name: " + currentUser.getNom(),
                    "Surname: " + currentUser.getPrenom(),
                    "Role: " + currentUser.getRole(),
                    "Email: " + currentUser.getEmail()
            );
            profileDetailsList.setItems(profileDetails);
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

    @FXML
    private void handleEditProfileNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit_profile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            if (currentUser != null) {
                editProfileController.initialize(currentUser);
            } else {
                showAlert("User data is not available.", Alert.AlertType.ERROR);
            }

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load edit profile view: " + e.getMessage(), Alert.AlertType.ERROR);
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
            if (currentUser != null) {
                passwordController.setUser(currentUser);
            } else {
                showAlert("User data is not available.", Alert.AlertType.ERROR);
            }

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

    @FXML
    private void openReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reclamation.fxml"));
            Parent root = loader.load();

            ReclamationController controller = loader.getController();
            if (currentUser != null) {
                controller.setUser(currentUser);
            } else {
                showAlert("User data is not available.", Alert.AlertType.ERROR);
            }

            // Get the current stage and replace the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUnauthorizedAccess() {
        showAlert("Unauthorized access. You do not have permission to view this page.", Alert.AlertType.ERROR);
        // Optionally, navigate to an error page or logout
    }
}
