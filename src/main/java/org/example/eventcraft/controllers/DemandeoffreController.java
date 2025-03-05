package org.example.eventcraft.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.eventcraft.SERVICE.CrudDemande;
import org.example.eventcraft.entites.DemandeOffre;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DemandeoffreController {

    @FXML
    private ListView<HBox> demandeOffreListView;  // This needs to match the FXML fx:id

    @FXML
    private Button addButton;

    private CrudDemande crudDemandeOffre = new CrudDemande();
    private ObservableList<HBox> demandeOffreList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadDemandeOffres(); // Load demandes when the page initializes
    }

    private void loadDemandeOffres() {
        try {
            List<DemandeOffre> demandes = crudDemandeOffre.afficher(); // Fetch demandes from database
            demandeOffreList.clear();

            for (DemandeOffre demande : demandes) {
                HBox itemBox = createDemandeOffreItem(demande); // Create UI item for each demande
                demandeOffreList.add(itemBox); // Add to the list
            }

            demandeOffreListView.setItems(demandeOffreList); // Bind list to ListView
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les demandes", e.getMessage());
        }
    }

    private HBox createDemandeOffreItem(DemandeOffre demandeOffre) {
        // Main container with padding and styling
        VBox demandeBox = new VBox(10);
        demandeBox.setPadding(new Insets(15));
        demandeBox.setStyle("-fx-background-color: #fdf4d8; -fx-border-color: #ccc; -fx-border-radius: 10px; -fx-box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);");

        // Ensure the VBox takes full width
        demandeBox.setPrefWidth(demandeOffreListView.getWidth()); // Dynamically set width
        demandeBox.setMaxWidth(Double.MAX_VALUE);

        // Labels with improved styling
        Label statutLabel = new Label("Statut: " + demandeOffre.getStatutDemande());
        statutLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statutLabel.setTextFill(Color.web("#333"));

        Label dateLabel = new Label("Date: " + demandeOffre.getDateDemande());
        dateLabel.setFont(Font.font("Arial", 14));
        dateLabel.setTextFill(Color.web("#666"));

        Label userLabel = new Label("Utilisateur: " + demandeOffre.getUser());
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setTextFill(Color.web("#666"));

        Label offreLabel = new Label("Offre: " + demandeOffre.getOffre());
        offreLabel.setFont(Font.font("Arial", 14));
        offreLabel.setTextFill(Color.web("#666"));

        // Buttons with better UI
        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        modifyButton.setStyle("-fx-background-color: #8b7355; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px; -fx-padding: 5px 10px;");
        deleteButton.setStyle("-fx-background-color: #8b7355; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px; -fx-padding: 5px 10px;");

        modifyButton.setOnAction(event -> handleModifier(demandeOffre));
        deleteButton.setOnAction(event -> handleSupprimer(demandeOffre));

        // Button container
        HBox buttonBox = new HBox(10, modifyButton, deleteButton);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        // Add elements to VBox
        demandeBox.getChildren().addAll(statutLabel, dateLabel, userLabel, offreLabel, buttonBox);

        // Wrap in an HBox for alignment and ensure full width
        HBox hbox = new HBox(demandeBox);
        HBox.setHgrow(demandeBox, Priority.ALWAYS); // Make it expand fully
        return hbox;
    }

    private void handleModifier(DemandeOffre demandeOffre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/modifierDemande.fxml"));
            Parent root = loader.load();

            // Get the ModifierController instance
            ModifierDemandeController controller = loader.getController();

            // Pass the selected demande to the ModifierController
            controller.setDemandeDetails(demandeOffre);

            // Create and show the Modifier window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Refresh the list after the modification window is closed
            stage.setOnHidden(event -> loadDemandeOffres());

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la modification", e.getMessage());
        }
    }


    private void handleSupprimer(DemandeOffre demandeOffre) {
        int idDemande = demandeOffre.getIdDemande();

        if (idDemande <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID de demande invalide", "L'ID de la demande est invalide.");
            return; // Exit if ID is invalid
        }

        try {
            // Delete the demande from the database
            crudDemandeOffre.supprimer(idDemande);

            // Show success alert
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande supprimée", "La demande a été supprimée avec succès.");

            // Refresh list after deletion
            loadDemandeOffres();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la demande", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        // Display an alert to the user
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Add this method to navigate to the Ajouter DemandeOffre page
    @FXML
    private void handleAddDemandeOffre(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/AjoutDemande.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Demande Offre");
            stage.setScene(new Scene(root));

            // When the window closes, reload the demandes
            stage.setOnHidden(e -> loadDemandeOffres());

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page d'ajout", e.getMessage());
        }
    }


    @FXML
    private void handleReturn(ActionEvent actionEvent) {
        // Close the current window
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
    }


}
