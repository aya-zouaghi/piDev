package com.eventcraft.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.eventcraft.model.Evenement;
import com.eventcraft.service.EvenementService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EditEventController {

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
    private TextField locationField;

    @FXML
    private ComboBox<String> salleComboBox;

    @FXML
    private Button returnButton;

    @FXML
    private Button confirmerButton;

    private EvenementService evenementService = new EvenementService();
    private String eventId; // Store the event ID for editing

    @FXML
    public void initialize() {
        try {
            List<String> salleNames = evenementService.getSalleNames();
            salleComboBox.setItems(FXCollections.observableArrayList(salleNames));

            // Print the items to verify
            System.out.println("Salle names: " + salleNames);

            if (!salleNames.isEmpty()) {
                salleComboBox.setValue(salleNames.get(0));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des noms de salle: " + e.getMessage());
        }
    }




    // Method to set the event ID and load its data
    public void setEventId(String eventId) {
        this.eventId = eventId;
        loadEventData(eventId);
    }

    private void loadEventData(String eventId) {
        try {
            Evenement evenement = evenementService.getEventById(Integer.parseInt(eventId));
            if (evenement != null) {
                titreField.setText(evenement.getTitre());
                descriptionField.setText(evenement.getDescription_evenement());
                imageField.setText(evenement.getImage());
                dateDebutField.setText(evenement.getDate_debut().toString());
                dateFinField.setText(evenement.getDate_fin().toString());
                locationField.setText(evenement.getLocation());

                // Set the selected salle in the ComboBox
                String salleName = evenementService.getSalleNameById(evenement.getsalleId());
                salleComboBox.setValue(salleName);
            } else {
                System.err.println("Erreur : Événement non trouvé avec l'ID " + eventId);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des données de l'événement: " + e.getMessage());
        }
    }


    @FXML
    public void handleConfirmer(ActionEvent event) {
        try {
            // Retrieve updated values from the form
            String titre = titreField.getText();
            String description = descriptionField.getText();
            String image = imageField.getText();
            LocalDate dateDebut = LocalDate.parse(dateDebutField.getText());
            LocalDate dateFin = LocalDate.parse(dateFinField.getText());
            String location = locationField.getText();
            String salleName = salleComboBox.getValue();

            // Get salle ID from the selected salle name
            int salleId = evenementService.getSalleIdByName(salleName);

            if (salleId == -1) {
                System.err.println("Erreur : la salle avec le nom " + salleName + " n'existe pas.");
                return;
            }

            // Create the updated Evenement object
            Evenement evenement = new Evenement(
                    Integer.parseInt(eventId), // Use the existing event ID
                    titre,
                    description,
                    image,
                    location,
                    dateDebut,
                    dateFin,
                    salleId
            );

            // Update the event in the database
            evenementService.modifier(evenement);

            System.out.println("✅ Événement modifié avec succès !");

        } catch (NumberFormatException e) {
            System.err.println("Erreur : Les dates doivent être au format valide.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification de l'événement : " + e.getMessage());
        }
    }

    @FXML
    public void openModifierEvent(ActionEvent event) {
        loadFXML("/view/ModifierEvent.fxml", "Modifier un Événement", event);
    }

    private void loadFXML(String fxmlPath, String title, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}