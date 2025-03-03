package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.service.ReclamationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReclamationController {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;

    private User user;
    private final ReclamationService reclamationService;

    public ReclamationController() {
        this.reclamationService = new ReclamationService();
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("User set in ReclamationController: " + user.getIdUser());
    }

    @FXML
    private void submitReclamation(ActionEvent event) {
        if (user == null) {
            showAlert("User not set. Please log in again.", Alert.AlertType.ERROR);
            return;
        }

        String titre = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = "Complaint";

        if (titre.isEmpty() || description.isEmpty()) {
            showAlert("Both title and description are required.", Alert.AlertType.WARNING);
            return;
        }

        // Check for profanity in the description
        if (containsProfanity(description)) {
            showAlert("Description contains inappropriate language. Please revise.", Alert.AlertType.WARNING);
            return;
        }

        boolean isSubmitted = reclamationService.submitReclamation(user, titre, description, type);
        if (isSubmitted) {
            showAlert("Reclamation submitted successfully.", Alert.AlertType.INFORMATION);
            clearFields();
        } else {
            showAlert("Failed to submit reclamation. Please try again later.", Alert.AlertType.ERROR);
        }
    }

    private boolean containsProfanity(String text) {
        try {
            String apiUrl = "https://profanity-filter-by-api-ninjas.p.rapidapi.com/v1/profanityfilter?text=" + text.replace(" ", "%20");
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-rapidapi-host", "profanity-filter-by-api-ninjas.p.rapidapi.com");
            connection.setRequestProperty("x-rapidapi-key", "0bf6cbe6a4mshfe53ff9a0d75f62p1d9553jsn49d1604d3783");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();
                reader.close();
                // Assuming the API returns a JSON object with a "contains_profanity" field
                // You may need to parse the JSON response accordingly
                return response.contains("true"); // Adjust based on actual API response
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void handleProfileNavigation(ActionEvent event) {
        navigateToWithUserData("/view/profile.fxml", event);
    }

    @FXML
    private void handleViewReclamationNavigation(ActionEvent event) {
        navigateToWithUserData("/view/view_reclamation.fxml", event);
    }

    @FXML
    private void handleViewResponsesNavigation(ActionEvent event) {
        navigateToWithUserData("/view/view_responses.fxml", event);
    }

    private void navigateToWithUserData(String fxmlFile, ActionEvent event) {
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
            showAlert("Failed to load view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
