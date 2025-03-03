package com.eventcraft.controller;

import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;
import com.eventcraft.service.GoogleAuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @FXML
    private Button googleSignInButton; // Add this to your FXML file

    private final UserDAO userDAO = new UserDAO();
    private final GoogleAuthService googleAuthService = new GoogleAuthService();

    @FXML
    public void initialize() {
        // Initialize any necessary components
    }

    @FXML
    public void handleSignUp() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleChoiceBox.getValue();

        // Validation for empty fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "All fields are required!");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showAlert("Error", "Invalid email format!");
            return;
        }

        // Check if email already exists in the database
        if (userDAO.emailExists(email)) {
            showAlert("Error", "This email is already registered!");
            return;
        }

        // Validate password strength
        if (!isValidPassword(password)) {
            showAlert("Error", "Password must be at least 8 characters long and contain both letters and digits.");
            return;
        }

        // Create a new User object
        User newUser = new User(lastName, firstName, password, "active", role, email);

        // Insert user into the database
        boolean success = userDAO.insertUser(newUser);

        if (success) {
            showAlert("Success", "User registered successfully!");
            clearFields();
            // Navigate to login page or home page
            navigateToLogin();
        } else {
            showAlert("Error", "Failed to register user.");
        }
    }

    @FXML
    public void handleGoogleSignIn() {
        try {
            User user = googleAuthService.signInWithGoogle();
            if (user != null) {
                showAlert("Success", "Successfully signed in with Google!");

                // Navigate to the appropriate page based on user role
                navigateToAppropriateView(user);
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to sign in with Google: " + e.getMessage());
        }
    }

    private void navigateToAppropriateView(User user) {
        try {
            // Depending on the user role, navigate to different pages
            String viewPath = "/view/dashboard.fxml"; // Default view

            if ("admin".equalsIgnoreCase(user.getRole())) {
                viewPath = "/view/adminDashboard.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            Parent view = loader.load();

            // Pass the user to the controller if needed
            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).initData(user);
            }

            Scene scene = new Scene(view);

            // Apply styles
            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Switch to the appropriate scene
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Navigation error: " + e.getMessage());
        }
    }

    // Helper method to validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Helper method to validate password (minimum 8 characters, contains both letters and digits)
    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*[0-9].*");
    }

    // Helper method to display alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear the form fields
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        roleChoiceBox.setValue(null);
    }

    @FXML
    public void handleLogin() {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent loginView = loader.load();
            Scene loginScene = new Scene(loginView);

            // Apply styles.css
            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            loginScene.getStylesheets().add(css);

            // Switch to login scene
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}