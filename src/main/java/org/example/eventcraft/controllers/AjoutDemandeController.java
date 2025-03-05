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
    private Sms smsService; // SMS service instance

    public AjoutDemandeController() {
        this.crudDemande = new CrudDemande();
        this.smsService = new Sms(); // Initialize the Sms service
    }

    // Method to handle Add DemandeOffre button click
    @FXML
    private void handleAddDemande() {
        try {
            // Validate user input for userField and offreField
            String userInput = userField.getText();
            String offreInput = offreField.getText();

            if (userInput.matches("\\d+") && offreInput.matches("\\d+")) {
                int userId = Integer.parseInt(userInput);   // User ID
                int offreId = Integer.parseInt(offreInput);  // Offer ID
                String statut = statutField.getText();       // Status
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

                // Send SMS to the user after adding the DemandeOffre
                String phoneNumber = "+21629444051"; // Replace with the actual user's phone number
                String message = "Your DemandeOffre has been successfully added!";
                smsService.sendSms(phoneNumber, message);  // Send SMS notification

            } else {
                System.out.println("Invalid input: User ID and Offer ID must be numeric.");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle invalid input (non-numeric values for user ID and offer ID)
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }
}
