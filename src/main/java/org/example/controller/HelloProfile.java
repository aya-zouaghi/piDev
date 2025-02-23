/*package org.example.controller;


import SERVICE.ReservationSalleService;
import SERVICE.SalleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.entities.Reservationsalle;
import org.example.entities.Salle;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HelloProfile {

    @FXML
    private VBox createditReservationForm;

    @FXML
    private DatePicker editDateDebutPicker;

    @FXML
    private DatePicker editDateFinPicker;

    @FXML
    private TextField titreField;

    @FXML
    private TextField capaciteField;

    @FXML
    private TextArea equipementsField;

    @FXML
    private TextField localisationField;

    @FXML
    private CheckBox disponibiliteCheckBox;

    @FXML
    private TextField imageField;

    @FXML
    private ListView<VBox> listViewSalles;

    @FXML
    private VBox formulaireVBox;

    private String imagePath;
    private Salle salleEnCours = null;
    private Reservationsalle reservationEnCours;

    private final SalleService salleService = new SalleService();
    private final ReservationSalleService reservationSalleService = new ReservationSalleService();
    private final int userId = 1; // Remplacez par l'ID de l'utilisateur connecté

    @FXML
    public void initialize() {
        if (editDateDebutPicker == null || editDateFinPicker == null || createditReservationForm == null) {
            System.err.println("FXML elements are not initialized!");
        } else {
            System.out.println("FXML elements initialized successfully.");
        }

        try {
            // Charger les salles par défaut
            loadSallesInListView();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des salles : " + e.getMessage());
            showErrorDialog("Erreur lors du chargement des salles. Veuillez réessayer.");
        }
    }

    @FXML
    private void handleAjouterSalleClick(ActionEvent event) {
        boolean isVisible = formulaireVBox.isVisible();
        formulaireVBox.setVisible(!isVisible);
    }

    @FXML
    private void handleBrowseAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imageField.setText(file.getName());
            imagePath = file.getAbsolutePath();
        }
    }

    @FXML
    private void handleAjouterOuEditerSalle() {
        String titre = titreField.getText();
        String capaciteText = capaciteField.getText();
        String equipements = equipementsField.getText();
        String localisation = localisationField.getText();
        boolean disponibilite = disponibiliteCheckBox.isSelected();

        if (titre.isEmpty() || capaciteText.isEmpty() || equipements.isEmpty() || imagePath == null || localisation.isEmpty()) {
            showErrorDialog("Tous les champs doivent être remplis.");
            return;
        }

        int capacite;
        try {
            capacite = Integer.parseInt(capaciteText);
        } catch (NumberFormatException e) {
            showErrorDialog("La capacité doit être un nombre valide.");
            return;
        }

        try {
            if (salleEnCours == null) {
                Salle nouvelleSalle = new Salle(titre, capacite, equipements, disponibilite, imagePath, localisation, userId,qualite,prix);
                salleService.ajouter(nouvelleSalle);
                showSuccessDialog("Salle ajoutée avec succès !");
            } else {
                salleEnCours.setNomSalle(titre);
                salleEnCours.setCapacite(capacite);
                salleEnCours.setEquipement(equipements);
                salleEnCours.setLocationSalle(localisation);
                salleEnCours.setDisponibilite(disponibilite);
                salleEnCours.setImageSalle(imagePath);

                showSuccessDialog("Salle modifiée avec succès !");
                salleEnCours = null;
            }

            resetFormFields();
            loadSallesInListView();
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de l'opération : " + e.getMessage());
        }
    }

    @FXML
    private void handleAfficherSallesClick() {
        loadSallesInListView();
    }

    @FXML
    private void handleAfficherReservationsClick() {
        loadReservationsInListView();
    }

    @FXML
    private void handlereserverSallesClick() {
        loadReserSallesInListView();
    }

    private void resetFormFields() {
        titreField.clear();
        capaciteField.clear();
        equipementsField.clear();
        localisationField.clear();
        disponibiliteCheckBox.setSelected(false);
        imageField.clear();
        imagePath = null;
    }

    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadSallesInListView() {
        try {
            List<Salle> salles = salleService.afficher();
            listViewSalles.getItems().clear();

            for (Salle salle : salles) {
                VBox salleVBox = createSalleVBox(salle);
                listViewSalles.getItems().add(salleVBox);
            }

            listViewSalles.setPrefHeight(listViewSalles.getItems().size() * 100);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createSalleVBox(Salle salle) {
        VBox salleVBox = new VBox(5);
        salleVBox.setStyle("-fx-border-color: #8D598F; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        ImageView imageView = new ImageView(new Image(new File(salle.getImageSalle()).toURI().toString()));
        imageView.setFitHeight(112);
        imageView.setFitWidth(213);
        imageView.setPreserveRatio(true);

        Label nomSalleLabel = new Label(salle.getNomSalle());
        nomSalleLabel.setStyle("-fx-font-weight: bold;");

        Label localisationLabel = new Label(salle.getLocationSalle());
        Label capaciteLabel = new Label("Capacité: " + salle.getCapacite() + " personnes");

        HBox buttonBox = new HBox(10);
        Button editButton = new Button("Editer");
        Button deleteButton = new Button("Supprimer");

        editButton.setStyle("-fx-background-color: #8D598F; -fx-text-fill: #FFFFFF;");
        deleteButton.setStyle("-fx-background-color: #8D598F; -fx-text-fill: #FFFFFF;");

        deleteButton.setOnAction(event -> handleDeleteSalle(salle, salleVBox));
        buttonBox.getChildren().addAll(editButton, deleteButton);

        salleVBox.getChildren().addAll(imageView, nomSalleLabel, localisationLabel, capaciteLabel, buttonBox);
        editButton.setOnAction(event -> handleEditSalle(salle));

        return salleVBox;
    }

    private void handleEditSalle(Salle salle) {
        salleEnCours = salle;
        titreField.setText(salle.getNomSalle());
        capaciteField.setText(String.valueOf(salle.getCapacite()));
        equipementsField.setText(salle.getEquipement());
        localisationField.setText(salle.getLocationSalle());
        disponibiliteCheckBox.setSelected(salle.isDisponibilite());
        imagePath = salle.getImageSalle();
        imageField.setText(new File(salle.getImageSalle()).getName());
        formulaireVBox.setVisible(true);
    }

    private void handleDeleteSalle(Salle salle, VBox salleVBox) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette salle ?");
        alert.setContentText("Cette action est irréversible.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                salleService.supprimer(salle.getIdSalle());
                listViewSalles.getItems().remove(salleVBox);
                showSuccessDialog("Salle supprimée avec succès !");
            } catch (SQLException e) {
                showErrorDialog("Erreur lors de la suppression de la salle : " + e.getMessage());
            }
        }
    }

    private void loadReserSallesInListView() {
        try {
            List<Salle> salles = salleService.afficher();
            listViewSalles.getItems().clear();

            for (Salle salle : salles) {
                VBox salleVBox = reserverSalleVBox(salle);
                listViewSalles.getItems().add(salleVBox);
            }

            listViewSalles.setPrefHeight(listViewSalles.getItems().size() * 160);
            listViewSalles.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
            listViewSalles.setMaxHeight(600);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox reserverSalleVBox(Salle salle) {
        VBox salleVBox = new VBox(5);
        salleVBox.setStyle("-fx-border-color: #8D598F; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        ImageView imageView = new ImageView(new Image(new File(salle.getImageSalle()).toURI().toString()));
        imageView.setFitHeight(112);
        imageView.setFitWidth(213);
        imageView.setPreserveRatio(true);

        Label nomSalleLabel = new Label(salle.getNomSalle());
        nomSalleLabel.setStyle("-fx-font-weight: bold;");

        Label localisationLabel = new Label(salle.getLocationSalle());
        Label capaciteLabel = new Label("Capacité: " + salle.getCapacite() + " personnes");

        HBox buttonBox = new HBox(5);
        Button reservButton = new Button("Réserver");
        Button deleteButton = new Button("Supprimer");

        reservButton.setStyle("-fx-background-color: #CBA979; -fx-text-fill: #FFFFFF;");
        deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFFFFF;");

        buttonBox.getChildren().addAll(reservButton, deleteButton);

        VBox reservationForm = new VBox(10);
        reservationForm.setStyle("-fx-padding: 15; -fx-background-color: #f1f1f1; -fx-border-radius: 5;");
        reservationForm.setVisible(false);

        Label startDateLabel = new Label("Date de début:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("Date de fin:");
        DatePicker endDatePicker = new DatePicker();
        Button confirmReservationButton = new Button("Confirmer la réservation");
        confirmReservationButton.setStyle("-fx-background-color: #CBA979; -fx-text-fill: #FFFFFF;");

        reservationForm.getChildren().addAll(startDateLabel, startDatePicker, endDateLabel, endDatePicker, confirmReservationButton);

        VBox editForm = new VBox(10);
        editForm.setStyle("-fx-padding: 15; -fx-background-color: #f1f1f1; -fx-border-radius: 5;");
        editForm.setVisible(false);

        TextField nomField = new TextField(salle.getNomSalle());
        TextField locationField = new TextField(salle.getLocationSalle());
        TextField capaciteField = new TextField(String.valueOf(salle.getCapacite()));
        Button saveEditButton = new Button("Enregistrer");
        saveEditButton.setStyle("-fx-background-color: #CBA979; -fx-text-fill: #FFFFFF;");

        editForm.getChildren().addAll(new Label("Nom:"), nomField, new Label("Localisation:"), locationField, new Label("Capacité:"), capaciteField, saveEditButton);

        salleVBox.getChildren().addAll(imageView, nomSalleLabel, localisationLabel, capaciteLabel, buttonBox, reservationForm, editForm);

        reservButton.setOnAction(event -> reservationForm.setVisible(true));

        confirmReservationButton.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate != null && endDate != null && !startDate.isAfter(endDate)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Réservation Confirmée");
                alert.setHeaderText(null);
                alert.setContentText("Réservation confirmée du " + startDate + " au " + endDate);
                alert.showAndWait();

                startDatePicker.setValue(null);
                endDatePicker.setValue(null);
                reservationForm.setVisible(false);
            } else {
                showErrorDialog("Veuillez sélectionner des dates valides.");
            }
        });

        saveEditButton.setOnAction(event -> {
            String newNom = nomField.getText();
            String newLocation = locationField.getText();
            int newCapacite;
            try {
                newCapacite = Integer.parseInt(capaciteField.getText());
            } catch (NumberFormatException e) {
                showErrorDialog("La capacité doit être un nombre valide.");
                return;
            }

            salle.setNomSalle(newNom);
            salle.setLocationSalle(newLocation);
            salle.setCapacite(newCapacite);

            nomSalleLabel.setText(newNom);
            localisationLabel.setText(newLocation);
            capaciteLabel.setText("Capacité: " + newCapacite + " personnes");

            editForm.setVisible(false);
        });

        deleteButton.setOnAction(event -> {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation");
            confirmDialog.setHeaderText("Voulez-vous vraiment supprimer cette salle ?");
            confirmDialog.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                ((VBox) salleVBox.getParent()).getChildren().remove(salleVBox);
            }
        });

        return salleVBox;
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadReservationsInListView() {
        try {
            List<Reservationsalle> reservations = reservationSalleService.getReservationsByUserId(userId);
            listViewSalles.getItems().clear();

            for (Reservationsalle reservation : reservations) {
                VBox reservationVBox = createReservationVBox(reservation);
                listViewSalles.getItems().add(reservationVBox);
            }

            listViewSalles.setPrefHeight(listViewSalles.getItems().size() * 100);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createReservationVBox(Reservationsalle reservation) {
        VBox reservationVBox = new VBox(5);
        reservationVBox.setStyle("-fx-border-color: #8D598F; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        try {
            Salle salle = salleService.getById(reservation.getSalle());

            if (salle != null) {
                ImageView imageView = new ImageView(new Image(new File(salle.getImageSalle()).toURI().toString()));
                imageView.setFitHeight(112);
                imageView.setFitWidth(213);
                imageView.setPreserveRatio(true);

                Label nomSalleLabel = new Label("Salle: " + salle.getNomSalle());
                nomSalleLabel.setStyle("-fx-font-weight: bold;");

                Label dateDebutLabel = new Label("Date de début: " + reservation.getDate_debut());
                Label dateFinLabel = new Label("Date de fin: " + reservation.getDate_fin());

                HBox buttonBox = new HBox(10);
                Button editButton = new Button("Editer");
                Button deleteButton = new Button("Supprimer");

                editButton.setStyle("-fx-background-color: #8D598F; -fx-text-fill: #FFFFFF;");
                deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFFFFF;");

                deleteButton.setOnAction(event -> handleDeleteReservation(reservation, reservationVBox));
                editButton.setOnAction(event -> createditReservationForm.setVisible(true));

                buttonBox.getChildren().addAll(editButton, deleteButton);

                reservationVBox.getChildren().addAll(imageView, nomSalleLabel, dateDebutLabel, dateFinLabel, buttonBox);

                editButton.setOnAction(event -> handleEditReservation(reservation));
            } else {
                Label errorLabel = new Label("Salle non trouvée");
                reservationVBox.getChildren().add(errorLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Erreur lors du chargement des informations de la salle");
            reservationVBox.getChildren().add(errorLabel);
        }

        return reservationVBox;
    }

    private void handleDeleteReservation(Reservationsalle reservation, VBox reservationVBox) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
        alert.setContentText("Cette action est irréversible.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                reservationSalleService.supprimer(reservation.getId_reservation());
                listViewSalles.getItems().remove(reservationVBox);
                showSuccessDialog("Réservation supprimée avec succès !");
            } catch (SQLException e) {
                showErrorDialog("Erreur lors de la suppression de la réservation : " + e.getMessage());
            }
        }
    }
    private void createEditReservationForm(Reservationsalle reservation) {
        // Créer un VBox pour le formulaire d'édition
        VBox editReservationForm = new VBox(10);
        editReservationForm.setAlignment(Pos.CENTER);
        editReservationForm.setVisible(false); // Masquer le formulaire par défaut

        // Ajouter un label pour le titre
        Label titleLabel = new Label("Modifier la Réservation");
        titleLabel.setStyle("-fx-font-weight: bold;");
        editReservationForm.getChildren().add(titleLabel);

        // Ajouter les DatePicker pour les dates de début et de fin
        DatePicker editDateDebutPicker = new DatePicker();
        editDateDebutPicker.setPromptText("Date de début");
        editReservationForm.getChildren().add(editDateDebutPicker);

        DatePicker editDateFinPicker = new DatePicker();
        editDateFinPicker.setPromptText("Date de fin");
        editReservationForm.getChildren().add(editDateFinPicker);

        // Pré-remplir les DatePicker avec les dates actuelles de la réservation
        editDateDebutPicker.setValue(reservation.getDate_debut().toLocalDate());
        editDateFinPicker.setValue(reservation.getDate_fin().toLocalDate());

        // Ajouter un bouton pour enregistrer les modifications
        Button saveButton = new Button("Enregistrer");
        saveButton.setOnAction(event -> handleSaveEditReservation(reservation, editDateDebutPicker, editDateFinPicker));
        editReservationForm.getChildren().add(saveButton);

        // Ajouter le formulaire d'édition au layout principal (par exemple, à la fin de la ListView)
        listViewSalles.getItems().add(editReservationForm);
    }

    private void handleSaveEditReservation(Reservationsalle reservation, DatePicker editDateDebutPicker, DatePicker editDateFinPicker) {
        LocalDate newStartDate = editDateDebutPicker.getValue();
        LocalDate newEndDate = editDateFinPicker.getValue();

        if (newStartDate != null && newEndDate != null && !newStartDate.isAfter(newEndDate)) {
            try {
                reservation.setDate_debut(java.sql.Date.valueOf(newStartDate));
                reservation.setDate_fin(java.sql.Date.valueOf(newEndDate));

                // Sauvegarder les modifications dans la base de données
                reservationSalleService.modifier(reservation);

                showSuccessDialog("Réservation modifiée avec succès !");
                loadReservationsInListView(); // Mettre à jour l'affichage
            } catch (SQLException e) {
                showErrorDialog("Erreur lors de la modification de la réservation : " + e.getMessage());
            }
        } else {
            showErrorDialog("Veuillez sélectionner des dates valides.");
        }
    }

    private void handleEditReservation(Reservationsalle reservation) {
        if (editDateDebutPicker == null || editDateFinPicker == null) {
            showErrorDialog("Erreur: Les champs de date ne sont pas initialisés.");
            return;
        }

        reservationEnCours = reservation;

        editDateDebutPicker.setValue(reservation.getDate_debut().toLocalDate());
        editDateFinPicker.setValue(reservation.getDate_fin().toLocalDate());

        createditReservationForm.setVisible(true);
    }
}
*/