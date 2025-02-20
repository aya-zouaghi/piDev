package com.eventcraft.controller;

import com.eventcraft.model.Participation;
import com.eventcraft.service.ParticipantService;
import com.eventcraft.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AjoutParticipantController {

    @FXML
    private TextField evenementField;

    @FXML
    private TextField dateInscriptionField;

    @FXML
    private TextField statutField;

    @FXML
    private Button submitButton;

    private final ParticipantService participantService = new ParticipantService();

    @FXML
    public void handleAjouterParticipant(ActionEvent event) {
        int loggedInUserId = SessionManager.getUserId();

        if (loggedInUserId == -1) {
            System.err.println("❌ Aucun utilisateur connecté !");
            return;
        }

        try {
            int evenementId = Integer.parseInt(evenementField.getText());
            Date dateInscription = new SimpleDateFormat("yyyy-MM-dd").parse(dateInscriptionField.getText());
            String statut = statutField.getText();

            Participation participation = new Participation(0, loggedInUserId, evenementId, dateInscription, statut);
            participantService.ajouter(participation);

            System.out.println("✅ Participation ajoutée avec succès !");
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de l'événement invalide !");
        } catch (ParseException e) {
            System.err.println("❌ Format de date invalide ! Utilisez YYYY-MM-DD.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du participant !");
            e.printStackTrace();
        }
    }


    @FXML
    public void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Participation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Participants");
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur de retour vers Participation.fxml");
            e.printStackTrace();
        }
    }
}
