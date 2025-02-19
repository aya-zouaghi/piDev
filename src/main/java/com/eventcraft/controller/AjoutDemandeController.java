package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import com.eventcraft.service.CrudDemande;
import com.eventcraft.model.DemandeOffre;

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
            // Get user input
            String userText = userField.getText().trim();
            String offreText = offreField.getText().trim();
            String statut = statutField.getText().trim();

            // Validate User ID (Must be a number)
            if (!userText.matches("\\d+")) {
                System.out.println("Invalid User ID: " + userText);
                return;  // Stop execution
            }

            // Validate Offer ID (Must be a number)
            if (!offreText.matches("\\d+")) {
                System.out.println("Invalid Offer ID: " + offreText);
                return;  // Stop execution
            }

            int userId = Integer.parseInt(userText);  // Convert to integer
            int offreId = Integer.parseInt(offreText);  // Convert to integer

            // Validate Date
            if (datePicker.getValue() == null) {
                System.out.println("Invalid Date: Date is required.");
                return;
            }

            Date dateDemande = java.sql.Date.valueOf(datePicker.getValue());  // Convert to Date

            // Create a new DemandeOffre object
            DemandeOffre newDemande = new DemandeOffre(userId, offreId, statut, dateDemande);

            // Add the DemandeOffre to the database
            crudDemande.ajouter(newDemande);

            // Clear fields
            userField.clear();
            offreField.clear();
            statutField.clear();
            datePicker.setValue(null);

            System.out.println("Demande added successfully: " + newDemande);

        } catch (SQLException e) {
            e.printStackTrace();  // Handle database errors
        }
    }
}
