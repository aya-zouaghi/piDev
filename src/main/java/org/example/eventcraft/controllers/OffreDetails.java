package org.example.eventcraft.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.eventcraft.SERVICE.Crudoffre;
import org.example.eventcraft.entites.Offre;

import java.io.IOException;
import java.sql.SQLException;

public class OffreDetails {

    @FXML
    private Label lblTitre;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblType;
    @FXML
    private Label lblMontant;
    @FXML
    private Label lblDateExp;
    @FXML
    private Label lblEvenement;
    @FXML
    private Label lblUser;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;

    private Offre selectedOffre;
    private Crudoffre crudOffre = new Crudoffre();
    private OffreController offreController; // Add this field

    public void setOffre(Offre selectedOffre) {
        if (selectedOffre != null) {
            this.selectedOffre = selectedOffre;

            lblTitre.setText("Titre: " + selectedOffre.getTitreOffre());
            lblDescription.setText("Description: " + selectedOffre.getDescriptionOffre());
            lblType.setText("Type: " + selectedOffre.getTypeOffre());
            lblMontant.setText("Montant: " + String.format("%.2f", selectedOffre.getMontant())); // Format to 2 decimal places
            lblDateExp.setText("Date Exp: " + selectedOffre.getDateExp().toString());
            lblEvenement.setText("Evenement: " + selectedOffre.getEvenement());
            lblUser.setText("User: " + selectedOffre.getUser());

            // Add styling to labels
            lblTitre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");
            lblDescription.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 5 0;");
            lblType.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 5 0;");
            lblMontant.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 5 0;");
            lblDateExp.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 5 0;");
            lblEvenement.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 5 0;");
            lblUser.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 5 0;");
        }
    }

    // Add this method to set the OffreController
    public void setOffreController(OffreController offreController) {
        this.offreController = offreController;
    }

    @FXML
    public void supprimerOffre(ActionEvent actionEvent) {
        if (selectedOffre != null) {
            try {
                crudOffre.supprimer(selectedOffre.getIdOffre());
                showAlert("Succès", "L'offre a été supprimée avec succès.", Alert.AlertType.INFORMATION);

                // Refresh the offer list in OffreController
                if (offreController != null) {
                    offreController.refreshOffres();
                }

                Stage stage = (Stage) lblTitre.getScene().getWindow();
                stage.close();

            } catch (SQLException e) {
                showAlert("Erreur", "Une erreur est survenue lors de la suppression.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            showAlert("Erreur", "Aucune offre sélectionnée à supprimer.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void modifierOffre(ActionEvent actionEvent) {
        if (selectedOffre != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/modifier.fxml"));
                Parent root = loader.load();

                ModifierController modifierController = loader.getController();
                modifierController.setOffreDetails(selectedOffre);

                Stage stage = new Stage();
                stage.setTitle("Modifier Offre");
                stage.setScene(new javafx.scene.Scene(root));
                stage.show();

                Stage currentStage = (Stage) lblTitre.getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Erreur", "Aucune offre sélectionnée pour modification.", Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }}