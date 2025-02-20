package com.eventcraft.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class EvenementController {

    @FXML
    private ImageView logoImage;

    public void handleAjouter(ActionEvent event) {
        loadFXML("/view/AjouterEvent.fxml", "Ajouter Événement", event);
    }
    private void loadFXML(String fxmlPath, String title, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur de chargement: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void handleSupprimer(ActionEvent event) {
        loadFXML("/view/SupprimerEvent.fxml", "Supprimer un Événement", event);
    }

    public void handleModifier(ActionEvent event) {
        loadFXML("/view/ModifierEvent.fxml", "Modifier un Événement", event);
    }


    @FXML
    public void handleQuitter(ActionEvent event) {
        System.out.println("Fermeture forcée de l'application");
        Platform.exit(); // Optionnel : tente une fermeture propre d'abord
        System.exit(0); // Termine le processus Java
    }
}

