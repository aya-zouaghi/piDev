package com.eventcraft.controller;

import com.eventcraft.model.Salle;
import com.eventcraft.service.ReservationSalleService;
import com.eventcraft.service.SalleService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatbotController {
    @FXML private VBox chatContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;
    @FXML private ScrollPane scrollPane;

    private SalleService salleService = new SalleService();
    private ReservationSalleService reservationService = new ReservationSalleService();
    private int step = 0; // Pour suivre l'étape du dialogue
    private int capacity = 0;
    private String equipment = "";
    private double budget = 0;
    private String location = "";
    private Date reservationDate = null;

    @FXML
    public void initialize() {
        startConversation();

        // Ajouter un gestionnaire d'événements pour la touche Entrée
        userInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage(); // Appeler la méthode sendMessage() lorsque la touche Entrée est pressée
            }
        });
    }

    private void startConversation() {
        appendChatbotMessage("Bonjour ! Je suis votre assistante AI pour vous aider à trouver la salle parfaite.");
        appendChatbotMessage("Combien de personnes doivent être accueillies ?");
    }

    @FXML
    private void sendMessage() {
        String message = userInput.getText().trim();
        if (!message.isEmpty()) { // Ne pas envoyer de message vide
            appendUserMessage(message);
            userInput.clear();

            switch (step) {
                case 0:
                    handleCapacityInput(message);
                    break;
                case 1:
                    handleEquipmentInput(message);
                    break;
                case 2:
                    handleBudgetInput(message);
                    break;
                case 3:
                    handleLocationInput(message);
                    break;
                case 4:
                    handleReservationDateInput(message);
                    break;
                case 5:
                    handleNewSearchRequest(message);
                    break;
            }
        }
    }

    private void handleCapacityInput(String message) {
        try {
            capacity = Integer.parseInt(message);
            if (capacity <= 0) {
                appendChatbotMessage("La capacité doit être un nombre positif. Veuillez réessayer.");
            } else {
                appendChatbotMessage("Très bien. Avez-vous besoin d'équipements spécifiques, comme un micro ou un projecteur ?");
                step++;
            }
        } catch (NumberFormatException e) {
            appendChatbotMessage("Veuillez entrer un nombre valide pour la capacité.");
        }
    }

    private void handleEquipmentInput(String message) {
        equipment = message.toLowerCase();
        appendChatbotMessage("Quel est votre budget maximum pour la location de la salle ?");
        step++;
    }

    private void handleBudgetInput(String message) {
        try {
            budget = Double.parseDouble(message);
            if (budget <= 0) {
                appendChatbotMessage("Le budget doit être un nombre positif. Veuillez réessayer.");
            } else {
                appendChatbotMessage("Dans quelle ville souhaitez-vous trouver une salle ?");
                step++;
            }
        } catch (NumberFormatException e) {
            appendChatbotMessage("Veuillez entrer un nombre valide pour le budget.");
        }
    }

    private void handleLocationInput(String message) {
        location = message.toLowerCase();
        appendChatbotMessage("Quelle date souhaitez-vous pour la réservation ? (Format : JJ/MM/AAAA)");
        step++;
    }

    private void handleReservationDateInput(String message) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            reservationDate = dateFormat.parse(message);
            appendChatbotMessage("Je recherche des salles correspondant à vos critères...");
            findSalles();
            step++; // Passer à l'étape suivante pour demander une nouvelle recherche
        } catch (ParseException e) {
            appendChatbotMessage("Veuillez entrer une date valide au format JJ/MM/AAAA.");
        }
    }

    private void handleNewSearchRequest(String message) {
        if (message.equalsIgnoreCase("oui")) {
            // Réinitialiser les critères et recommencer
            capacity = 0;
            equipment = "";
            budget = 0;
            location = "";
            reservationDate = null;
            step = 0;
            appendChatbotMessage("Très bien, recommençons.");
            startConversation();
        } else if (message.equalsIgnoreCase("non")) {
            appendChatbotMessage("Merci d'avoir utilisé notre service. À bientôt !");
            // Fermer la fenêtre ou désactiver le chatbot
        } else {
            appendChatbotMessage("Veuillez répondre par 'Oui' ou 'Non'.");
        }
    }

    private void findSalles() {
        try {
            List<Salle> salles = salleService.afficher();
            boolean exactMatchFound = false;
            boolean suggestionFound = false;

            // Normaliser les critères de recherche
            String normalizedLocation = location.trim().toLowerCase();
            String normalizedEquipment = equipment.trim().toLowerCase();

            // Rechercher des salles correspondant exactement aux critères
            for (Salle salle : salles) {
                String salleLocation = salle.getLocationSalle().trim().toLowerCase();
                String salleEquipment = salle.getEquipement().trim().toLowerCase();

                // Vérifier si la salle correspond exactement aux critères
                boolean matchesCapacity = salle.getCapacite() >= capacity;
                boolean matchesBudget = salle.getPrix() <= budget;
                boolean matchesLocation = salleLocation.contains(normalizedLocation);
                boolean matchesEquipment = salleEquipment.contains(normalizedEquipment);
                boolean isAvailable = reservationService.verifierDisponibilite(
                        salle.getIdSalle(),
                        new java.sql.Date(reservationDate.getTime()),
                        new java.sql.Date(reservationDate.getTime())
                );

                if (matchesCapacity && matchesBudget && matchesLocation && matchesEquipment && isAvailable) {
                    if (!exactMatchFound) {
                        appendChatbotMessage("Voici une salle qui correspond exactement à vos critères :");
                        exactMatchFound = true;
                    }
                    appendSalleDetails(salle);
                }
            }

            // Si aucune salle ne correspond exactement, chercher des suggestions
            if (!exactMatchFound) {
                appendChatbotMessage("Aucune salle ne correspond exactement à vos critères. Voici quelques suggestions :");

                for (Salle salle : salles) {
                    String salleLocation = salle.getLocationSalle().trim().toLowerCase();
                    String salleEquipment = salle.getEquipement().trim().toLowerCase();

                    // Vérifier si la salle répond à la plupart des critères
                    boolean matchesLocation = salleLocation.contains(normalizedLocation);
                    boolean matchesEquipment = salleEquipment.contains(normalizedEquipment);
                    boolean matchesBudget = salle.getPrix() <= budget * 1.2; // Budget jusqu'à 20% supérieur
                    boolean matchesCapacity = salle.getCapacite() >= capacity * 0.8; // Capacité jusqu'à 20% inférieure

                    // Vérifier la disponibilité de la salle à la date demandée
                    boolean isAvailable = reservationService.verifierDisponibilite(
                            salle.getIdSalle(),
                            new java.sql.Date(reservationDate.getTime()),
                            new java.sql.Date(reservationDate.getTime())
                    );

                    // Afficher la salle uniquement si elle est disponible
                    if ((matchesLocation || matchesEquipment) && matchesBudget && matchesCapacity && isAvailable) {
                        appendSalleDetails(salle);
                        suggestionFound = true;
                    }
                }

                if (!suggestionFound) {
                    appendChatbotMessage("Désolé, aucune suggestion n'est disponible pour le moment.");
                }
            }

            appendChatbotMessage("Souhaitez-vous effectuer une nouvelle recherche ? (Oui/Non)");
        } catch (SQLException e) {
            appendChatbotMessage("Une erreur s'est produite lors de la recherche des salles.");
            e.printStackTrace();
        }
    }

    private void appendChatbotMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-background-color: #8D598F; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #FFFFFF; -fx-font-family: Arial; -fx-font-size: 14px;");
        label.setMaxWidth(300);
        label.setWrapText(true);

        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche
        hbox.setStyle("-fx-padding: 5;");

        chatContainer.getChildren().add(hbox);
        scrollToBottom();
    }

    private void appendUserMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-background-color: #B19BB2; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #FFFFFF; -fx-font-family: Arial; -fx-font-size: 14px;");
        label.setMaxWidth(300);
        label.setWrapText(true);

        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_RIGHT); // Aligner à droite
        hbox.setStyle("-fx-padding: 5;");

        chatContainer.getChildren().add(hbox);
        scrollToBottom();
    }

    private void appendSalleDetails(Salle salle) {
        String salleDetails = String.format(
                "Nom: %s\nCapacité: %d\nÉquipements: %s\nLocalisation: %s\nPrix: %.2f DT",
                salle.getNomSalle(), salle.getCapacite(), salle.getEquipement(), salle.getLocationSalle(), salle.getPrix()
        );
        Label label = new Label(salleDetails);
        label.setStyle("-fx-background-color: #8D598F; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #333333; -fx-font-family: Arial; -fx-font-size: 14px;");
        label.setMaxWidth(300);
        label.setWrapText(true);

        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche
        hbox.setStyle("-fx-padding: 5;");

        chatContainer.getChildren().add(hbox);
        scrollToBottom();
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(1.0); // Faire défiler vers le bas
        });
    }
}