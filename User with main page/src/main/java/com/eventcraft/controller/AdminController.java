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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController extends BaseController {

    @FXML
    private ImageView profileImage;

    @FXML
    private Label userNameLabel;

    @FXML
    private ListView<String> profileDetailsList;

    private User currentUser;

    @FXML
    public void initialize() {
        // Ensure only admins can access this page
        if (!SessionManager.isAdmin()) {
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
                    "👤  Name                 " + currentUser.getNom(),
                    "👥  Surname              " + currentUser.getPrenom(),
                    "🎭  Role                 " + currentUser.getRole(),
                    "📧  Email                " + currentUser.getEmail()
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
    private void handleBackNavigation(ActionEvent event) {
        navigateBack((Node) event.getSource());
    }

    @FXML
    private void handleEditProfileNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit_profile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            editProfileController.initialize(currentUser);

            Scene newScene = new Scene(root);
            pushSceneToHistory(((Node) event.getSource()).getScene());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(newScene);
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

                Scene newScene = new Scene(root);
                pushSceneToHistory(((Node) event.getSource()).getScene());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(newScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Failed to load admin dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            handleUnauthorizedAccess();
        }
    }

    @FXML
    private void handlePassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Password.fxml"));
            Parent passwordPage = loader.load();

            // Pass user data to PasswordController
            PasswordController passwordController = loader.getController();
            passwordController.setUser(currentUser);

            Scene newScene = new Scene(passwordPage);
            pushSceneToHistory(((Node) event.getSource()).getScene());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(newScene);
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

    @Override
    protected void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
