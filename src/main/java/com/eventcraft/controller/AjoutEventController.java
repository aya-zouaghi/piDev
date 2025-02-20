package com.eventcraft.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import com.eventcraft.model.Evenement;
import com.eventcraft.service.EvenementService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AjoutEventController {

    @FXML
    private ImageView logoImage;

    @FXML
    private TextField titreField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField imageField;

    @FXML
    private TextField dateDebutField;

    @FXML
    private TextField dateFinField;

    @FXML
    private TextField userField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField salleField;

    @FXML
    private Button returnButton;

    @FXML
    private Button homeButton;

    @FXML
    private ComboBox<String> salleComboBox;

    private EvenementService evenementService = new EvenementService();

    @FXML
    public void initialize() {
        try {
            List<String> salleNames = evenementService.getSalleNames();
            salleComboBox.setItems(FXCollections.observableArrayList(salleNames));
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des noms de salle: " + e.getMessage());
        }
    }

    @FXML
    public void openEvenements(ActionEvent event) {
        loadFXML("/view/Evenements.fxml", "Gestion Événements", event);
    }

    @FXML
    public void openHome(ActionEvent event) {
        loadFXML("/view/Home.fxml", "Accueil", event);
    }

    @FXML
    public void ajouterEvenement(ActionEvent event) {
        try {
            String titre = titreField.getText();
            String description = descriptionField.getText();
            String image = imageField.getText();
            LocalDate dateDebut = LocalDate.parse(dateDebutField.getText());
            LocalDate dateFin = LocalDate.parse(dateFinField.getText());
            String location = locationField.getText();

            // Retrieve the selected salle name from the ComboBox
            String salleName = salleComboBox.getValue();

            if (salleName == null || salleName.isEmpty()) {
                System.err.println("Erreur : Aucune salle sélectionnée.");
                return;
            }

            // Get the salle ID by its name
            int salleId = evenementService.getSalleIdByName(salleName);

            if (salleId == -1) {
                System.err.println("Erreur : la salle avec le nom " + salleName + " n'existe pas.");
                return;
            }

            // Create the Evenement object and add it to the database
            Evenement evenement = new Evenement(titre, description, image, location, dateDebut, dateFin, salleId);
            evenementService.ajouter(evenement);

            System.out.println("✅ Événement ajouté avec succès !");

        } catch (NumberFormatException e) {
            System.err.println("Erreur : Les dates doivent être au format valide.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de l'événement : " + e.getMessage());
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
