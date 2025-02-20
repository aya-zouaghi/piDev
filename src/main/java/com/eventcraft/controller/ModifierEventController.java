package com.eventcraft.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class ModifierEventController {

    @FXML
    private TextField idField;

    @FXML
    public void handleReturn(ActionEvent event) {
        loadFXML("/view/Evenements.fxml", "Gérer les Événements", event);
    }

    @FXML
    public void handleHome(ActionEvent event) {
        loadFXML("/view/Home.fxml", "Accueil", event);
    }

    @FXML
    public void handleModifier(ActionEvent event) {
        // Vérifier si l'ID est saisi
        if (!idField.getText().isEmpty()) {
            naviguerVersEditEvent(event);
        } else {
            System.out.println("Veuillez saisir un ID valide !");
        }
    }
    private void naviguerVersEditEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditEvent.fxml"));
            Parent root = loader.load();

            // Passer l'ID à EditEventController si nécessaire
            EditEventController editController = loader.getController();
            editController.setEventId(idField.getText()); // À implémenter dans EditEventController

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadFXML(String fxmlPath, String title, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur de navigation: " + fxmlPath);
            e.printStackTrace();
        }
    }
}