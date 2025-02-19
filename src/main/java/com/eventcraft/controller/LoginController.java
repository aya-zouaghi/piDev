package com.eventcraft.controller;

import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;
import com.eventcraft.util.SessionManager;
import javafx.scene.Node;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

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

        // DEBUGGING: Check user role and password comparison
        System.out.println("User found: " + user.getNom() + " " + user.getPrenom());
        System.out.println("Role: " + user.getRole());
        System.out.println("Entered Password: " + password);
        System.out.println("Stored Password: " + user.getPassword());

        if (user.getPassword().equals(password)) { // If using hashed passwords, update this check
            SessionManager.setUser(user); // Set the user in the session
            System.out.println("Session User Set: " + SessionManager.getUser().getRole());

            try {
                // Load the Welcome.fxml page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Welcome.fxml"));
                Parent welcomeView = loader.load();

                Scene welcomeScene = new Scene(welcomeView);
                String css = getClass().getResource("/styles/styles.css").toExternalForm();
                welcomeScene.getStylesheets().add(css);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(welcomeScene);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error loading welcome page.");
            }
        } else {
            showAlert("Invalid username or password.");
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signup.fxml"));
            Parent signUpView = loader.load();
            Scene signUpScene = new Scene(signUpView);

            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            signUpScene.getStylesheets().add(css);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(signUpScene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading signup page.");
        }
    }
}
