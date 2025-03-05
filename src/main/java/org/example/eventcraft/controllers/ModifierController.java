package org.example.eventcraft.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.eventcraft.SERVICE.Crudoffre;
import org.example.eventcraft.entites.Offre;

import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;

public class ModifierController {

    @FXML
    private TextField tf_idOffre;
    @FXML
    private TextField tf_titre;
    @FXML
    private TextArea tf_description;
    @FXML
    private TextField tf_type;
    @FXML
    private TextField tf_montant;
    @FXML
    private TextField tf_evenement;
    @FXML
    private TextField tf_user;
    @FXML
    private DatePicker dp_dateExp;
    @FXML
    private TextField tf_rating; // Add TextField or another component for rating

    private Crudoffre crudOffre = new Crudoffre();
    private int currentOffreId; // To track the offer being modified

    public void setOffreDetails(Offre offre) {
        currentOffreId = offre.getIdOffre();
        tf_idOffre.setText(String.valueOf(offre.getIdOffre()));
        tf_titre.setText(offre.getTitreOffre());
        tf_description.setText(offre.getDescriptionOffre());
        tf_type.setText(offre.getTypeOffre());
        tf_montant.setText(String.valueOf(offre.getMontant()));
        tf_evenement.setText(String.valueOf(offre.getEvenement()));
        tf_user.setText(String.valueOf(offre.getUser()));
        tf_rating.setText(String.valueOf(offre.getRating())); // Set the current rating

        if (offre.getDateExp() != null) {
            dp_dateExp.setValue(offre.getDateExp().toLocalDate());
        }
    }

    @FXML
    public void modifierOffre(ActionEvent actionEvent) {
        try {
            String titre = tf_titre.getText().trim();
            String description = tf_description.getText().trim();
            String type = tf_type.getText().trim();
            String montantText = tf_montant.getText().trim();
            String evenementText = tf_evenement.getText().trim();
            String userText = tf_user.getText().trim();
            String ratingText = tf_rating.getText().trim(); // Get the rating from the UI

            if (titre.isEmpty() || description.isEmpty() || type.isEmpty() || montantText.isEmpty() || evenementText.isEmpty() || userText.isEmpty() || ratingText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs vides", "Veuillez remplir tous les champs.");
                return;
            }

            float montant = Float.parseFloat(montantText);
            int evenement = Integer.parseInt(evenementText);
            int user = Integer.parseInt(userText);
            double rating = Double.parseDouble(ratingText); // Convert rating to double

            if (dp_dateExp.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Date manquante", "Veuillez sélectionner une date d'expiration.");
                return;
            }

            Date dateExp = Date.from(dp_dateExp.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create an updated Offre object
            Offre offre = new Offre(currentOffreId, titre, description, type, montant, dateExp, evenement, user, rating); // Pass rating

            // Call Crudoffre to update the offer
            crudOffre.modifier(offre);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification réussie", "L'offre a été modifiée avec succès.");
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide", "Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Modification échouée", e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) tf_titre.getScene().getWindow();
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
