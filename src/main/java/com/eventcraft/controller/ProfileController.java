package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.util.SessionManager;
import com.eventcraft.util.NavigationHistory;  // New utility class we'll create
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public class ProfileController {

    @FXML
    private ImageView profileImage;

    @FXML
    private Label userNameLabel;

    @FXML
    private ListView<String> profileDetailsList;

    // Navigation controls
    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private HBox navigationBar;

    private User currentUser;

    @FXML
    public void initialize() {
        // Set up navigation buttons


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






    public void setUser(User user) {
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

    public void setUserData(User user) {
        this.currentUser = user;
        if (user != null) {
            populateFields();
        } else {
            showAlert("User data is not available.", Alert.AlertType.ERROR);
        }
    }

    public void populateFields() {
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
    private void handleEditProfileNavigation(ActionEvent event) {
        try {
            // Add current page to navigation history before navigating
            NavigationHistory.addPage("/view/profile.fxml");

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
            // Add current page to navigation history before navigating
            NavigationHistory.addPage("/view/profile.fxml");

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

        // Delete the tokens directory
        Path tokensDirectory = Paths.get("C:\\Users\\Baha Ayadi\\Desktop\\event_craft\\tokens");
        try {
            deleteDirectory(tokensDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to delete tokens directory: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        // Clear navigation history on logout
        NavigationHistory.clearHistory();

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

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    @FXML
    private void openReclamation(ActionEvent event) {
        try {
            // Add current page to navigation history before navigating
            NavigationHistory.addPage("/view/profile.fxml");

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
    @FXML
    private void handleMainNavigation(ActionEvent event) {
        try {
            // Add current page to navigation history before navigating
            NavigationHistory.addPage("/view/profile.fxml");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Parent root = loader.load();

            // Assuming there's a MainController to initialize
            MainDashboardController mainDashboardController = loader.getController();
            if (currentUser != null) {
                mainDashboardController.setUser(currentUser);
            } else {
                showAlert("User data is not available.", Alert.AlertType.ERROR);
            }

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load main view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void handleUnauthorizedAccess() {
        showAlert("Unauthorized access. You do not have permission to view this page.", Alert.AlertType.ERROR);
        // Optionally, navigate to an error page or logout
    }
}