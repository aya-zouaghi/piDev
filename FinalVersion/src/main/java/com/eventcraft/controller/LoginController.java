package com.eventcraft.controller;

import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;
import com.eventcraft.service.GoogleAuthService;
import com.eventcraft.util.SessionManager;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private GoogleAuthService googleAuthService = new GoogleAuthService();

    @FXML
    public void handleSignIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in both username and password.");
            return;
        }

        User user = UserDAO.getUserByEmail(username);

        if (user == null) {
            showAlert("User not found.");
            return;
        }

        if ("Banned".equalsIgnoreCase(user.getStatutCompte())) {
            showAlert("Your account is banned.");
            return;
        }

        if (user.getPassword().equals(password)) {
            SessionManager.setUser(user);

            try {
                String fxmlPath = SessionManager.isAdmin() ? "/view/admin.fxml" : "/view/profile.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent mainPageView = loader.load();

                if (!SessionManager.isAdmin()) {
                    ProfileController profileController = loader.getController();
                    profileController.setUser(user);
                }

                Scene mainPageScene = new Scene(mainPageView);
                String css = getClass().getResource("/styles/styles.css").toExternalForm();
                mainPageScene.getStylesheets().add(css);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(mainPageScene);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error loading dashboard.");
            }
        } else {
            showAlert("Invalid username or password.");
        }
    }

    @FXML
    public void handleGoogleSignIn() {
        try {
            User googleUser = googleAuthService.signInWithGoogle();

            if (googleUser != null) {
                // Check if the user is banned
                if ("Banned".equalsIgnoreCase(googleUser.getStatutCompte())) {
                    showAlert("Your account is banned.");
                    return;
                }

                // If this is a new Google account, link it to the current user
                // This simplified implementation assumes you want to replace the current credentials
                // In a real app, you might want to link accounts instead

                googleUser.setPassword("GOOGLE_AUTH_USER");
                // Update user in database using your UserDAO

                SessionManager.setUser(googleUser);

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
                    Parent mainPageView = loader.load();

                    ProfileController profileController = loader.getController();
                    profileController.setUser(googleUser);

                    Scene mainPageScene = new Scene(mainPageView);
                    String css = getClass().getResource("/styles/styles.css").toExternalForm();
                    mainPageScene.getStylesheets().add(css);

                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(mainPageScene);
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error loading dashboard.");
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            showAlert("Failed to connect Google account: " + e.getMessage());
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleSignUp(Event event) {
        navigateToPage(event, "/view/signup.fxml");
    }

    @FXML
    public void handleForgotPassword(Event event) {
        navigateToPage(event, "/view/forgot_password.fxml");
    }

    private void navigateToPage(Event event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Scene scene = new Scene(view);

            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading page.");
        }
    }
}
