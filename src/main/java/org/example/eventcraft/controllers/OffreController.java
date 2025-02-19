package org.example.eventcraft.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.eventcraft.SERVICE.Crudoffre;
import org.example.eventcraft.entites.Offre;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OffreController {

    @FXML
    private ListView<HBox> offreListView;
    @FXML
    private Button addButton;

    private Crudoffre crudOffre = new Crudoffre();
    private ObservableList<HBox> offreList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadOffres(); // Load offers when the page initializes
    }

    private void loadOffres() {
        try {
            List<Offre> offres = crudOffre.afficher(); // Fetch offers
            offreList.clear();

            for (Offre offre : offres) {
                HBox itemBox = createOffreItem(offre); // Create UI item for each offer
                offreList.add(itemBox); // Add to the list
            }

            offreListView.setItems(offreList); // Bind list to ListView
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les offres", e.getMessage());
            e.printStackTrace();
        }
    }

    private HBox createOffreItem(Offre offre) {
        // Concatenate all the attributes of the Offre to display in the label
        Label offreLabel = new Label("Titre: " + offre.getTitreOffre() +
                " | Description: " + offre.getDescriptionOffre() +
                " | Type: " + offre.getTypeOffre() +
                " | Montant: " + offre.getMontant() +
                " | Date Exp: " + offre.getDateExp() +
                " | Evenement: " + offre.getEvenement() +
                " | User: " + offre.getUser());

        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        // Style buttons
        modifyButton.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

        // Action on Modify button click
        modifyButton.setOnAction(event -> handleModifier(offre));

        // Action on Delete button click
        deleteButton.setOnAction(event -> handleSupprimer(offre));

        // Layout for each offer item
        HBox hbox = new HBox(10, offreLabel, modifyButton, deleteButton);
        return hbox;
    }


    private void handleModifier(Offre offre) {
        try {
            // Load Modifier FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/modifier.fxml"));
            Parent root = loader.load();

            // Get the ModifierController instance
            ModifierController controller = loader.getController();

            // Pass the selected offer to the ModifierController
            controller.setOffreDetails(offre);

            // Show the Modifier window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la modification", e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleSupprimer(Offre offre) {
        try {
            // Delete the offer
            crudOffre.supprimer(offre.getIdOffre());

            // Show success alert
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée", "L'offre a été supprimée avec succès.");

            // Refresh list after deletion
            loadOffres();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'offre", e.getMessage());
            e.printStackTrace();
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

    // Add this method to navigate to the Ajouter Offre page
    @FXML
    private void handleAddOffre(ActionEvent event) {
        try {
            // Load the "Ajouter Offre" FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/AjoutOffre.fxml"));
            Parent root = loader.load();

            // Open the "Ajouter Offre" window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page d'ajout", e.getMessage());
        }
    }
}
