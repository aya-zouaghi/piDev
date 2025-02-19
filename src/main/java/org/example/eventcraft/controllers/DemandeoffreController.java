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
import org.example.eventcraft.SERVICE.CrudDemande;
import org.example.eventcraft.entites.DemandeOffre;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DemandeoffreController {

    @FXML
    private ListView<HBox> demandeOffreListView;
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
        // Concatenate all the attributes of DemandeOffre to display in the label
        Label demandeLabel = new Label("Statut: " + demandeOffre.getStatutDemande() +
                " | Date Demande: " + demandeOffre.getDateDemande() +
                " | User: " + demandeOffre.getUser() +
                " | Offre: " + demandeOffre.getOffre());

        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        // Style buttons
        modifyButton.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

        // Action on Modify button click
        modifyButton.setOnAction(event -> handleModifier(demandeOffre));

        // Action on Delete button click
        deleteButton.setOnAction(event -> handleSupprimer(demandeOffre));

        // Layout for each demande item
        HBox hbox = new HBox(10, demandeLabel, modifyButton, deleteButton);
        return hbox;
    }

    private void handleModifier(DemandeOffre demandeOffre) {
        try {
            // Load Modifier FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/modifierDemande.fxml"));
            Parent root = loader.load();

            // Get the ModifierController instance
            ModifierDemandeController controller = loader.getController();

            // Pass the selected demande to the ModifierController
            controller.setDemandeDetails(demandeOffre);

            // Show the Modifier window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
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
            // Load the "Ajouter DemandeOffre" FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/AjoutDemande.fxml"));
            Parent root = loader.load();

            // Create a new Stage (window) for the Ajouter Demande page
            Stage stage = new Stage();
            stage.setTitle("Ajouter Demande Offre");  // You can set the title as needed
            stage.setScene(new Scene(root));
            stage.show();

            // Optionally, you can close the current window (if needed)
            Stage currentStage = (Stage) addButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page d'ajout", e.getMessage());
        }
    }

}
