package org.example.controller;


import SERVICE.ReservationSalleService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Reservationsalle;
import org.example.entities.Salle;

import java.sql.Date;
import java.sql.SQLException;

public class HelloReservation {


    @FXML
    private TextField evenementField;  // Champ pour l'événement
    @FXML
    private DatePicker dateDebutPicker;  // Date Picker pour la date de début
    @FXML
    private DatePicker dateFinPicker;  // Date Picker pour la date de fin
    @FXML
    private Button reserverButton;  // Bouton pour réserver

    private Salle salle;  // Salle passée depuis le HelloSalleController
    private final ReservationSalleService reservationService = new ReservationSalleService();

    @FXML
    public void initialize() {
        // Initialisation des composants si nécessaire
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    @FXML
    public void reserver() {
        try {
            // Récupérer les informations de réservation
            int evenementId = Integer.parseInt(evenementField.getText());
            Date dateDebut = Date.valueOf(dateDebutPicker.getValue());
            Date dateFin = Date.valueOf(dateFinPicker.getValue());

            // Créer l'objet Reservationsalle
            Reservationsalle reservation = new Reservationsalle(0, salle.getIdSalle(),  1, dateDebut, dateFin);

            // Ajouter la réservation dans la base de données
            reservationService.ajouter(reservation);

            // Afficher un message de succès
            System.out.println("Réservation effectuée avec succès !");
            // Fermer la fenêtre de réservation après soumission
            ((Stage) reserverButton.getScene().getWindow()).close();

        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la réservation : " + e.getMessage());
        }
    }
}
