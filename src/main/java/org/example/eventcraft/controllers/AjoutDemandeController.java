package org.example.eventcraft.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.example.eventcraft.SERVICE.CrudDemande;
import org.example.eventcraft.entites.DemandeOffre;

import java.sql.SQLException;
import java.util.Date;

public class AjoutDemandeController {

    @FXML
    private TextField userField;  // Input field for User ID

    @FXML
    private TextField offreField; // Input field for Offer ID

    @FXML
    private TextField statutField; // Input field for Status

    @FXML
    private DatePicker datePicker; // Input field for Date

    @FXML
    private Button addButton; // Button to trigger adding DemandeOffre

    private CrudDemande crudDemande;

    public AjoutDemandeController() {
        this.crudDemande = new CrudDemande();
    }

    // Method to handle Add DemandeOffre button click
    @FXML
    private void handleAddDemande() {
        try {
            // Retrieve input values from the form
            int userId = Integer.parseInt(userField.getText());   // User ID
            int offreId = Integer.parseInt(offreField.getText());  // Offer ID
            String statut = statutField.getText();                 // Status
            Date dateDemande = java.sql.Date.valueOf(datePicker.getValue());  // Date

            // Create a new DemandeOffre object
            DemandeOffre newDemande = new DemandeOffre(userId, offreId, statut, dateDemande);

            // Add the DemandeOffre to the database
            crudDemande.ajouter(newDemande);

            // Optionally: Clear the fields after insertion
            userField.clear();
            offreField.clear();
            statutField.clear();
            datePicker.setValue(null);

            // Display success message or feedback to the user (optional)
            System.out.println("Demande added successfully: " + newDemande);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle invalid input (non-numeric values for user ID and offer ID)
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }
}
