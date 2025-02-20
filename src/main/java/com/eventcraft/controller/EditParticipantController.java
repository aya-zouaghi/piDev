package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

public class EditParticipantController {

    @FXML
    private ImageView logoImage;

    @FXML
    private TextField userField;

    @FXML
    private TextField evenementField;

    @FXML
    private TextField dateInscriptionField;

    @FXML
    private TextField statutField;

    @FXML
    private Button returnButton;

    @FXML
    private Button confirmerButton;

    @FXML
    public void initialize() {
        // Initialisation des composants si nécessaire
    }

    @FXML
    public void openModifierParticipant(ActionEvent event) {
        // Logique pour revenir à la vue de modification du participant
        System.out.println("Retour à la modification du participant cliqué");
    }

    @FXML
    public void handleConfirmer(ActionEvent event) {
        // Logique pour confirmer la modification du participant
        System.out.println("Confirmation de la modification du participant");

        // Récupérer les valeurs des champs
        String user = userField.getText();
        String evenement = evenementField.getText();
        String dateInscription = dateInscriptionField.getText();
        String statut = statutField.getText();

        // Ajoutez ici la logique pour enregistrer les modifications
    }
}
