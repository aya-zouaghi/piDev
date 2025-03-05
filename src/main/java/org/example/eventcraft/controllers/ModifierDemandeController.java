package org.example.eventcraft.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.eventcraft.SERVICE.CrudDemande;
import org.example.eventcraft.entites.DemandeOffre;

import java.sql.SQLException;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.Date;

import java.time.LocalDate;

public class ModifierDemandeController {

    @FXML
    private TextField tf_idDemande;
    @FXML
    private TextField tf_user;
    @FXML
    private TextField tf_offre;
    @FXML
    private TextField tf_statutDemande;
    @FXML
    private DatePicker dp_dateDemande;

    private CrudDemande crudDemandeOffre = new CrudDemande();
    private int currentDemandeId;

    public void setDemandeDetails(DemandeOffre demande) {
        currentDemandeId = demande.getIdDemande();
        tf_idDemande.setText(String.valueOf(demande.getIdDemande()));
        tf_user.setText(String.valueOf(demande.getUser()));
        tf_offre.setText(String.valueOf(demande.getOffre()));
        tf_statutDemande.setText(demande.getStatutDemande());

        if (demande.getDateDemande() != null) {
            // Convert java.sql.Date to LocalDate for DatePicker
            java.sql.Date sqlDate = (java.sql.Date) demande.getDateDemande();
            LocalDate localDate = sqlDate.toLocalDate();  // Correct way to convert to LocalDate
            dp_dateDemande.setValue(localDate);
        }
    }


    @FXML
    public void modifierDemande(ActionEvent actionEvent) {
        try {
            String userText = tf_user.getText().trim();
            String offreText = tf_offre.getText().trim();
            String statut = tf_statutDemande.getText().trim();

            if (userText.isEmpty() || offreText.isEmpty() || statut.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs vides", "Veuillez remplir tous les champs.");
                return;
            }

            int user = Integer.parseInt(userText);
            int offre = Integer.parseInt(offreText);

            if (dp_dateDemande.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Date manquante", "Veuillez sélectionner une date.");
                return;
            }

            // Convert LocalDate from DatePicker to java.sql.Date
            LocalDate localDate = dp_dateDemande.getValue();
            java.sql.Date dateDemande = java.sql.Date.valueOf(localDate);  // Correct conversion to sql.Date

            // Create updated DemandeOffre object
            DemandeOffre demande = new DemandeOffre(currentDemandeId, user, offre, statut, dateDemande);

            // Call CrudDemandeOffre to update the demande
            crudDemandeOffre.modifier(demande);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification réussie", "La demande a été modifiée avec succès.");
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide", "Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Modification échouée", e.getMessage());
        }
    }


    private void closeWindow() {
        Stage stage = (Stage) tf_user.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
