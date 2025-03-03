package com.eventcraft.controller;

import com.eventcraft.model.Reclamation;
import com.eventcraft.model.User;
import com.eventcraft.service.ReclamationService;
import com.eventcraft.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.io.IOException;
import java.util.List;

public class ViewReclamationController {
    @FXML
    private ListView<Reclamation> reclamationsList;

    private User user;
    private final ReclamationService reclamationService;

    public ViewReclamationController() {
        this.reclamationService = new ReclamationService();
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("User set in ViewReclamationController: " + user.getIdUser());
        loadReclamations();
    }

    @FXML
    private void initialize() {
        System.out.println("ViewReclamationController initialized.");
        reclamationsList.setCellFactory(param -> new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);

                if (empty || reclamation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5); // 5 is the spacing between elements
                    container.setPadding(new Insets(10));

                    Label titleLabel = new Label("Title: " + reclamation.getTitre());
                    titleLabel.setStyle("-fx-font-weight: bold");

                    Label descriptionLabel = new Label("Description: " + reclamation.getDescription());
                    descriptionLabel.setWrapText(true);

                    Label statusLabel = new Label("Status: " + reclamation.getStatut());
                    statusLabel.setStyle("-fx-font-style: italic");

                    container.getChildren().addAll(titleLabel, descriptionLabel, statusLabel);
                    setGraphic(container);
                }
            }
        });
    }

    private void loadReclamations() {
        if (user == null) {
            showAlert("User not set. Cannot load reclamations.", Alert.AlertType.ERROR);
            return;
        }

        List<Reclamation> reclamations = reclamationService.getReclamationsByUser(user);
        ObservableList<Reclamation> observableList = FXCollections.observableArrayList(reclamations);
        reclamationsList.setItems(observableList);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void handleProfileNavigation(ActionEvent event) {
        String fxmlFile = SessionManager.isAdmin() ? "/view/admin.fxml" : "/view/profile.fxml";
        navigateToWithUserData(fxmlFile, event);
    }

    @FXML
    private void navigateToViewReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_reclamation.fxml"));
            Parent page = loader.load();

            ViewReclamationController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(page));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToWithUserData(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();

            if (SessionManager.isAdmin()) {
                AdminController adminController = loader.getController();
                adminController.setUserData(user);
            } else if (SessionManager.isClient()) {
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