package com.eventcraft.controller;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
                    VBox container = new VBox(10);
                    container.getStyleClass().add("reclamation-cell");
                    container.setPadding(new Insets(10));

                    // Title with icon
                    Label titleLabel = new Label("📝  " + reclamation.getTitre());
                    titleLabel.getStyleClass().add("reclamation-title");

                    // Description
                    Label descriptionLabel = new Label(reclamation.getDescription());
                    descriptionLabel.getStyleClass().add("reclamation-description");
                    descriptionLabel.setWrapText(true);

                    // Status with appropriate styling
                    Label statusLabel = new Label("Status: " + reclamation.getStatut());
                    statusLabel.getStyleClass().addAll("reclamation-status", getStatusStyleClass(reclamation.getStatut()));

                    // Add a separator
                    Separator separator = new Separator();
                    separator.setStyle("-fx-background-color: #F5E6D3;");

                    // Bottom row with date and delete button
                    HBox detailsBox = new HBox(10);
                    detailsBox.setAlignment(Pos.CENTER_LEFT);

                    // Spacer to push delete button to the right
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Delete button with trash emoji
                    Button deleteButton = new Button("❌");
                    deleteButton.getStyleClass().add("delete-button");
                    deleteButton.setStyle("-fx-text-fill: red; -fx-background-color: transparent;");
                    deleteButton.setOnAction(e -> handleDeleteReclamation(reclamation));

                    detailsBox.getChildren().addAll(statusLabel, spacer, deleteButton);

                    // Add all components to the container
                    container.getChildren().addAll(titleLabel, descriptionLabel, separator, detailsBox);

                    setGraphic(container);
                }
            }

            private String getStatusStyleClass(String status) {
                return switch (status.toLowerCase()) {
                    case "pending" -> "status-pending";
                    case "resolved" -> "status-resolved";
                    case "rejected" -> "status-rejected";
                    default -> "";
                };
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
    private void handleDeleteReclamation(Reclamation reclamation) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Reclamation");
        confirmAlert.setHeaderText("Delete Reclamation");
        confirmAlert.setContentText("Are you sure you want to delete this reclamation?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = reclamationService.deleteReclamation(reclamation.getId());
                if (deleted) {
                    loadReclamations(); // Refresh the list
                } else {
                    showAlert("Failed to delete reclamation.", Alert.AlertType.ERROR);
                }
            }
        });
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