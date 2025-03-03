package com.eventcraft.controller;
import com.eventcraft.model.CommandeDecoration;
import com.eventcraft.model.Decoration;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.eventcraft.service.ServiceCommandeDeco;
import java.io.IOException;
import java.time.LocalDate;

public class CommanderDecoController {
    @javafx.fxml.FXML
    private Label lblNom;
    @javafx.fxml.FXML
    private Label lblPrix;
    @javafx.fxml.FXML
    private TextField txtQuantite;
    @javafx.fxml.FXML
    private Pane paneCommande;
    @javafx.fxml.FXML
    private Button btnCommander;
    @javafx.fxml.FXML
    private Button btnAnnuler;
    private Decoration selectedDeco;
    private ServiceCommandeDeco serviceCommande = new ServiceCommandeDeco();
    @javafx.fxml.FXML
    private Button AfficherCommande;

    public void setDecoration(Decoration selectedDeco) {
        this.selectedDeco = selectedDeco;
        lblNom.setText(selectedDeco.getNom_decor());
        lblPrix.setText(String.format("%.2f TND", selectedDeco.getPrix()));
    }

    @javafx.fxml.FXML
    public void AnnulerCommande(ActionEvent actionEvent) {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    @javafx.fxml.FXML
    public void CommanderArticle(ActionEvent actionEvent) {
        if (selectedDeco == null) {
            showAlert("Erreur", "Aucune décoration sélectionnée.", Alert.AlertType.ERROR);
            return;
        }
        String quantiteStr = txtQuantite.getText();
        if (quantiteStr.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer une quantité.", Alert.AlertType.WARNING);
            return;
        }
        try {
            int quantite = Integer.parseInt(quantiteStr);
            if (quantite <= 0) {
                showAlert("Erreur", "Veuillez entrer une quantité valide.", Alert.AlertType.WARNING);
                return;
            }

            if (quantite > selectedDeco.getStock()) {
                showAlert("Erreur", "Stock insuffisant ! Stock disponible : " + selectedDeco.getStock(), Alert.AlertType.WARNING);
                return;
            }


        serviceCommande.ajouter(new CommandeDecoration(

                quantite,
                LocalDate.now(),
                selectedDeco.getPrix() * quantite,

                selectedDeco
        ));

        showAlert("Succès", "Commande passée avec succès !", Alert.AlertType.INFORMATION);

            // 🚀 Charger la page d'affichage des commandes après validation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AfficherCommande.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCommander.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
    } catch (NumberFormatException e) {
        showAlert("Erreur", "Veuillez entrer un nombre valide.", Alert.AlertType.WARNING);
    } catch (Exception e) {
        showAlert("Erreur", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
    }

    }
        private void showAlert(String title, String message, Alert.AlertType alertType) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

    @javafx.fxml.FXML
    public void AfficherCommande(ActionEvent actionEvent) {
       try{
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AfficherCommande.fxml"));
        Parent root = loader.load();

        // Remplacer la scène actuelle avec la nouvelle page
        Stage stage = (Stage) AfficherCommande.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Erreur", "Impossible de retourner à la liste des commandes.", Alert.AlertType.ERROR);
    }
    }
}
