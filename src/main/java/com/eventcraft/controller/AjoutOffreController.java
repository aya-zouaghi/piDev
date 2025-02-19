package com.eventcraft.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea; // Changed to TextArea
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.eventcraft.service.Crudoffre;
import com.eventcraft.model.Offre;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;

public class AjoutOffreController {

    @FXML
    private TextField tf_titre;
    @FXML
    private TextArea tf_description;  // Changed to TextArea
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
    public void AjouterOffre(ActionEvent actionEvent) {
        try {
            // Validate inputs
            String titre = tf_titre.getText().trim();
            String description = tf_description.getText().trim();  // TextArea to String
            String type = tf_type.getText().trim();
            String montantText = tf_montant.getText().trim();
            String evenementText = tf_evenement.getText().trim();
            String userText = tf_user.getText().trim();

            if (titre.isEmpty() || description.isEmpty() || type.isEmpty() || montantText.isEmpty() || evenementText.isEmpty() || userText.isEmpty()) {
                showError("Veuillez remplir tous les champs.");
                return;
            }

            float montant = Float.parseFloat(montantText); // Potential NumberFormatException if not a valid float
            int evenement = Integer.parseInt(evenementText); // Potential NumberFormatException
            int user = Integer.parseInt(userText); // Potential NumberFormatException

            if (dp_dateExp.getValue() == null) {
                showError("Veuillez sélectionner une date d'expiration.");
                return;
            }

            Date dateExp = Date.from(dp_dateExp.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            Offre offre = new Offre(titre, description, type, montant, dateExp, evenement, user);
            Crudoffre serviceOffre = new Crudoffre() {
            };
            serviceOffre.ajouter(offre);

            showSuccess("Offre ajoutée avec succès !");
        } catch (NumberFormatException e) {
            showError("Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException e) {
            showError("Erreur lors de l'ajout de l'offre : " + e.getMessage());
        }
    }

    @FXML
    public void AfficherOffres(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/views/AfficherOffres.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur de chargement de la page : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
