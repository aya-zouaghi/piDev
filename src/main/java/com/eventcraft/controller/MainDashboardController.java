package com.eventcraft.controller;

import com.eventcraft.model.User;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import com.eventcraft.util.SessionManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.util.Comparator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainDashboardController implements Initializable {


    @FXML
    private Button dashboardBtn;

    @FXML
    private Button sallesBtn;

    @FXML
    private Button offresBtn;

    @FXML
    private Button forumBtn;

    @FXML
    private Button decorationsBtn;

    @FXML
    private Button profileBtn;

    @FXML
    private Button notificationsBtn;

    @FXML
    private Button reclamationBtn;

    @FXML
    private Button logoutBtn;
    private User user;

    public void setUser(User user) {
        this.user = user;
        // Additional logic to handle the user object
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set active button styling
        setActiveButton(dashboardBtn);

        // You can load dynamic content here, such as:
        // - User's event statistics
        // - Recent activity data
        // - User information
    }

    @FXML
    private void handleSallesNavigation(ActionEvent event) {
        navigateToPage("profilsalle.fxml", event, "Gestion des salles");
    }

    @FXML
    private void handleOffresNavigation(ActionEvent event) {
        navigateToPage("OffresView.fxml", event, "Gestion des offres");
    }

    @FXML
    private void handleForumNavigation(ActionEvent event) {
        navigateToPage("ForumView.fxml", event, "Forum");
    }

    @FXML
    private void handleDecorationsNavigation(ActionEvent event) {
        navigateToPage("DecorationsView.fxml", event, "Gestion des décorations");
    }

    @FXML
    private void handleProfileNavigation(ActionEvent event) {
        // Remove the leading slash since we're adding it in navigateToPage
        navigateToPage("profile.fxml", event, "Mon profil");
    }

    @FXML
    private void handleNotificationsNavigation(ActionEvent event) {
        navigateToPage("NotificationsView.fxml", event, "Notifications");
    }

    @FXML
    private void handleReclamationNavigation(ActionEvent event) {
        navigateToWithUserData("/view/view_reclamation.fxml", event);
    }


    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();

        // Delete the tokens directory
        Path tokensDirectory = Paths.get("C:\\Users\\Baha Ayadi\\Desktop\\event_craft\\tokens");
        try {
            deleteDirectory(tokensDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to delete tokens directory: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        // Navigate to the login page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent loginPage = loader.load();
            Scene loginScene = new Scene(loginPage);
            // Load the CSS
            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            loginScene.getStylesheets().add(css);

            // Use a node that is guaranteed to be initialized
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Helper method to set active button styling
    private void setActiveButton(Button button) {
        // Remove active class from all buttons
        dashboardBtn.getStyleClass().remove("active");

        profileBtn.getStyleClass().remove("active");

        reclamationBtn.getStyleClass().remove("active");

        // Add active class to selected button
        button.getStyleClass().add("active");
    }

    // Helper method to navigate to different views
    private void navigateToPage(String fxmlFile, ActionEvent event, String title) {
        try {
            // Construct the URL for the FXML file
            URL resourceUrl = getClass().getResource("/view/" + fxmlFile);

            if (resourceUrl == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            // Pass the user object to the new controller if it implements UserAwareController
            Object controller = loader.getController();
            if (controller instanceof UserAwareController && user != null) {
                ((UserAwareController) controller).setUser(user);
            }

            // Get the current stage and update the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("EventCraft - " + title);

            // Obtenir les dimensions de l'écran
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            // Définir la taille de la fenêtre pour qu'elle corresponde aux dimensions de l'écran
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            // Définir la scène et afficher la fenêtre
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading page " + title + ": " + e.getMessage());
        }
    }    private void navigateToWithUserData(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();

            // Set user data in the new controller
            if (fxmlFile.equals("/view/view_reclamation.fxml")) {
                ViewReclamationController viewReclamationController = loader.getController();
                viewReclamationController.setUser(user);
            } else if (fxmlFile.equals("/view/view_responses.fxml")) {
                ViewResponsesController viewResponsesController = loader.getController();
                viewResponsesController.setUser(user);
            } else if (fxmlFile.equals("/view/profile.fxml")) {
                ProfileController profileController = loader.getController();
                profileController.setUserData(user);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(page));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }



    // Helper method to display errors
    private void showError(String message) {
        // You could implement this to show a dialog or alert
        System.err.println(message);

        // If you have an error dialog utility:
        // ErrorUtil.showErrorDialog("Erreur", message);
    }

    // Add methods to refresh dashboard data
    public void refreshStatistics() {
        // Code to fetch updated statistics from your service/database
        // Then update the UI elements with new data
    }

    public void refreshActivityFeed() {
        // Code to fetch recent activity data from your service/database
        // Then update the activity list with new items
    }
}

// Add this interface to handle controllers that need user information
interface UserAwareController {
    void setUser(User user);
}