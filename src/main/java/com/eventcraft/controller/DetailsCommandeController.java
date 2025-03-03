package com.eventcraft.controller;
import com.eventcraft.model.CommandeDecoration;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.eventcraft.service.ServiceCommandeDeco;
import java.io.IOException;

public class DetailsCommandeController {
    @javafx.fxml.FXML
    private Label lblDateCommande;
    @javafx.fxml.FXML
    private Label lblPrix;
    @javafx.fxml.FXML
    private Label lblDecoration;
    @javafx.fxml.FXML
    private Label lblQuantite;
    @javafx.fxml.FXML
    private Button btnSupprimer;

    private CommandeDecoration selectedCommande;
    private ServiceCommandeDeco serviceCommandeDeco = new ServiceCommandeDeco();
    private AfficherCommandeController afficherController;
    @javafx.fxml.FXML
    private Button retour;

    @javafx.fxml.FXML
    public void SupprimerCommande(ActionEvent actionEvent) {
        if (selectedCommande != null) {
            try {
                System.out.println(selectedCommande.getId_commande());
                serviceCommandeDeco.supprimer(selectedCommande.getId_commande());
                showAlert("Succès", "La commande a été supprimée avec succès.", Alert.AlertType.INFORMATION);
                if (afficherController != null) {
                    System.out.println("🔄 Rafraîchissement de la liste des commandes...");
                    afficherController.rafraichirAffichageCommande();
                }else {
                    System.out.println("⚠ afficherController est NULL !");
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AfficherCommande.fxml"));
                Parent root = loader.load();

                // Afficher la nouvelle scène
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des commandes");
                stage.show();
            } catch (Exception e) {

                e.printStackTrace();
            }
        } else {
            showAlert("Erreur", "Aucune commande sélectionnée à supprimer.", Alert.AlertType.WARNING);
        }
    }
    public void setCommande(CommandeDecoration selectedCommande) {
        if (selectedCommande != null) {
            this.selectedCommande = selectedCommande;
            lblQuantite.setText(String.valueOf(selectedCommande.getQuantité()));
            lblDateCommande.setText(String.valueOf(selectedCommande.getDate_commande()));
            lblPrix.setText(String.valueOf(selectedCommande.getPrix()) + "  dt");
            lblDecoration.setText(selectedCommande.getDecoration().getNom_decor());

        }
    }
    public void setAfficherController(AfficherCommandeController afficherController) {
        this.afficherController = afficherController;
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @javafx.fxml.FXML
    public void retourner(ActionEvent actionEvent) {
        try {
            // Charger la page AfficherCommande.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AfficherCommande.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la nouvelle
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des commandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
