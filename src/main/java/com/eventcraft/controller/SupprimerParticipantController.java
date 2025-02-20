package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

public class SupprimerParticipantController {

    @FXML
    private ImageView logoImage;

    @FXML
    private TextField idField;

    @FXML
    private Button returnButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button supprimerButton;

    @FXML
    public void initialize() {
        // Initialisation des composants si nécessaire
    }

    @FXML
    public void handleReturn(ActionEvent event) {
        // Logique pour revenir à la vue précédente
        System.out.println("Retour cliqué");
    }

    @FXML
    public void handleHome(ActionEvent event) {
        // Logique pour ouvrir la vue d'accueil
        System.out.println("Accueil cliqué");
    }

    @FXML
    public void handleSupprimer(ActionEvent event) {
        // Logique pour supprimer le participant
        String id = idField.getText();
        System.out.println("Suppression du participant avec l'ID: " + id);
        // Ajoutez ici la logique pour supprimer le participant
    }
}
