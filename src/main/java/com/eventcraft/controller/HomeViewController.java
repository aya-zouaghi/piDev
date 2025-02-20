package com.eventcraft.controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeViewController {

    @FXML
    private ImageView logoImage;

    @FXML
    private Button evenementButton;

    @FXML
    private Button participantButton;

    @FXML
    private Button quitterButton;

    @FXML
    public void initialize() {
        // Initialisation des composants si nécessaire
    }

    @FXML
    public void handleEvenement(ActionEvent event) {
        loadView("/view/Evenements.fxml", "Gérer Événements", event);
    }

    @FXML
    public void handleParticipant(ActionEvent event) {
        loadView("/view/Participation.fxml", "Gérer Participants", event);
    }

    @FXML

    public void handleQuitter(ActionEvent event) {
        System.out.println("Fermeture de l'application");
        //Platform.exit(); // Ferme proprement l'application JavaFX
        System.exit(0); // Optionnel pour forcer la fermeture du processus
    }

    private void loadView(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
