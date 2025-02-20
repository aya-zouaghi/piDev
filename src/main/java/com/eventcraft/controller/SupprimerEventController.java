package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import com.eventcraft.service.EvenementService;

import java.io.IOException;
import java.sql.SQLException;

public class SupprimerEventController {

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

    private EvenementService evenementService;

    public SupprimerEventController() {
        this.evenementService = new EvenementService();
    }

    @FXML
    public void initialize() {
        // Initialisation des composants si nécessaire
    }

    @FXML
    public void handleReturn(ActionEvent event) {
        loadFXML("/view/Evenements.fxml", "Gérer les Événements", event);
    }

    @FXML
    public void handleHome(ActionEvent event) {
        loadFXML("/view/Home.fxml", "Accueil", event);
    }

    @FXML
    public void handleSupprimer(ActionEvent event) {
        try {
            int idEvenement = Integer.parseInt(idField.getText());
            evenementService.supprimer(idEvenement);
            System.out.println("✅ Événement supprimé avec succès !");

            // Redirect to homeso.fxml after deletion
            loadFXML("/view/homeso.fxml", "Accueil", event);
        } catch (NumberFormatException e) {
            System.err.println("Erreur: ID invalide !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'événement !");
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