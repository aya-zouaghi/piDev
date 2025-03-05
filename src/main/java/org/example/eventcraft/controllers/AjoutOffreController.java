package org.example.eventcraft.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.eventcraft.SERVICE.Crudoffre;
import org.example.eventcraft.entites.Offre;

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
    private TextField tf_rating; // Added rating field

    @FXML
    public void AjouterOffre(ActionEvent actionEvent) {
        try {
            // Validate inputs
            String titre = tf_titre.getText().trim();
            String description = tf_description.getText().trim();
            String type = tf_type.getText().trim();
            String montantText = tf_montant.getText().trim();
            String evenementText = tf_evenement.getText().trim();
            String userText = tf_user.getText().trim();
            String ratingText = tf_rating.getText().trim(); // Get rating input

            if (titre.isEmpty() || description.isEmpty() || type.isEmpty() || montantText.isEmpty() || evenementText.isEmpty() || userText.isEmpty() || ratingText.isEmpty()) {
                showError("Veuillez remplir tous les champs.");
                return;
            }

            float montant = Float.parseFloat(montantText);
            int evenement = Integer.parseInt(evenementText);
            int user = Integer.parseInt(userText);
            double rating = Double.parseDouble(ratingText); // Convert rating to double

            if (dp_dateExp.getValue() == null) {
                showError("Veuillez sélectionner une date d'expiration.");
                return;
            }

            Date dateExp = Date.from(dp_dateExp.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            Offre offre = new Offre(titre, description, type, montant, dateExp, evenement, user, rating); // Pass rating
            Crudoffre serviceOffre = new Crudoffre();
            serviceOffre.ajouter(offre);

            showSuccess("Offre ajoutée avec succès !");

            // Close the current window (AjoutOffre) and open the OffreController
            Stage stage = (Stage) tf_titre.getScene().getWindow();
            stage.close();

            // Open OffreController (refresh the list of offers)
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("/org/example/eventcraft/views/Offre.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (NumberFormatException e) {
            showError("Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException e) {
            showError("Erreur lors de l'ajout de l'offre : " + e.getMessage());
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture de la page : " + e.getMessage());
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
