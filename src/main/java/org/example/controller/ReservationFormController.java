package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.Reservationsalle;
import SERVICE.ReservationSalleService;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationFormController {

    private int salleId; // ID de la salle à réserver
    private int userId;  // ID de l'utilisateur qui effectue la réservation
    private Stage stage; // Fenêtre du formulaire

    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private Button confirmButton;

    // Méthode pour initialiser les valeurs
    public void setSalleId(int salleId) {
        this.salleId = salleId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    private void handleConfirmButton() {
        try {
            if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
                System.out.println("Veuillez sélectionner une date de début et une date de fin.");
                return;
            }

            Date dateDebut = Date.valueOf(dateDebutPicker.getValue());
            Date dateFin = Date.valueOf(dateFinPicker.getValue());

            ReservationSalleService reservationService = new ReservationSalleService();

            // Vérifier la disponibilité avant d'ajouter la réservation
            if (reservationService.verifierDisponibilite(salleId, dateDebut, dateFin)) {
                Reservationsalle reservation = new Reservationsalle(0, salleId, userId, dateDebut, dateFin);
                reservationService.ajouter(reservation);
                System.out.println("Réservation ajoutée avec succès !");
                stage.close(); // Fermer la fenêtre après la réservation
            } else {
                System.out.println("La salle n'est pas disponible à cette date.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
