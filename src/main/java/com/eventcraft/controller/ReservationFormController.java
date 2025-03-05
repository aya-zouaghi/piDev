package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import com.eventcraft.model.Reservationsalle;
import com.eventcraft.service.ReservationSalleService;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

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
            // Vérifier que les dates sont sélectionnées
            if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez sélectionner une date de début et une date de fin.");
                return;
            }

            // Convertir les dates sélectionnées en objets Date
            Date dateDebut = Date.valueOf(dateDebutPicker.getValue());
            Date dateFin = Date.valueOf(dateFinPicker.getValue());

            // Vérifier que la date de début est dans le futur
            LocalDate today = LocalDate.now();
            if (dateDebutPicker.getValue().isBefore(today)) {
                showAlert(Alert.AlertType.WARNING, "Date invalide", "La date de début doit être dans le futur.");
                return;
            }

            // Vérifier que la date de fin est postérieure ou égale à la date de début
            if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
                showAlert(Alert.AlertType.WARNING, "Date invalide", "La date de fin doit être postérieure ou égale à la date de début.");
                return;
            }

            // Vérifier la disponibilité de la salle
            ReservationSalleService reservationService = new ReservationSalleService();
            if (!reservationService.verifierDisponibilite(salleId, dateDebut, dateFin)) {
                showAlert(Alert.AlertType.WARNING, "Indisponible", "La salle n'est pas disponible à cette date.");
                return;
            }

            // Afficher une boîte de dialogue de confirmation
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmer la réservation");
            confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir réserver cette salle ?");
            confirmationAlert.setContentText("Date de début : " + dateDebut + "\nDate de fin : " + dateFin);

            // Attendre la réponse de l'utilisateur
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            // Si l'utilisateur confirme la réservation
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Créer un objet Reservationsalle
                Reservationsalle reservation = new Reservationsalle(0, salleId, userId, dateDebut, dateFin);

                // Ajouter la réservation à la base de données
                reservationService.ajouter(reservation);

                // Afficher un message de succès
                showAlert(Alert.AlertType.INFORMATION, "Succès", "La réservation a été ajoutée avec succès !");

                // Fermer la fenêtre après la réservation
                stage.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la réservation : " + e.getMessage());
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