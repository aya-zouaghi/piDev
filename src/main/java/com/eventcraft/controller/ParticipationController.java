package com.eventcraft.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;

public class ParticipationController {

    @FXML
    private ImageView logoImage;

    @FXML
    public void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AjouterParticipant.fxml")); // Corrected path
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Participant");
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur de navigation vers AjouterParticipant.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSupprimer(ActionEvent actionEvent) {
        System.out.println("🔴 Suppression d'un participant...");
        // Add actual delete logic here
    }

    @FXML
    public void handleModifier(ActionEvent actionEvent) {
        System.out.println("✏️ Modification d'un participant...");
        // Add actual update logic here
    }

    @FXML
    public void handleQuitter(ActionEvent event) {
        System.out.println("🛑 Fermeture de l'application");
        Platform.exit();
        System.exit(0);
    }
}
