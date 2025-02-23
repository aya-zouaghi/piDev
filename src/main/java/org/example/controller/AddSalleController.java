package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import SERVICE.SalleService;
import org.example.entities.Salle;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AddSalleController {
    @FXML
    private TextField nomSalleField;

    @FXML
    private TextField capaciteField;

    @FXML
    private TextField equipementField;

    @FXML
    private Label imagePathLabel;

    @FXML
    private TextField locationSalleField;

    @FXML
    private ComboBox<String> qualiteComboBox;

    @FXML
    private TextField prixField;
    @FXML
    private ListView<String> imagesListView; // Pour afficher les noms des images sélectionnées

    private ObservableList<String> imagePaths = FXCollections.observableArrayList(); // Pour stocker les chemins des images

    private Stage stage;
    private SalleService salleService = new SalleService();
    private String imagePath; // Variable pour stocker le chemin de l'image

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        imagesListView.setItems(imagePaths); // Lier la ListView à la liste des chemins d'images
    }
    @FXML
    private void handleChooseImagesButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir des images");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Ouvrir le dialogue de sélection de fichiers multiples
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            // Ajouter les chemins des fichiers sélectionnés à la liste
            for (File file : selectedFiles) {
                imagePaths.add(file.getAbsolutePath());
            }
        }
    }

    @FXML
    private void handleAddButton() {
        try {
            // Vérifier que tous les champs sont remplis
            if (nomSalleField.getText().isEmpty() || capaciteField.getText().isEmpty() || equipementField.getText().isEmpty()
                    || imagePaths.isEmpty() || locationSalleField.getText().isEmpty()
                    || qualiteComboBox.getValue() == null || prixField.getText().isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.");
                return;
            }

            // Récupérer les valeurs des champs
            String nomSalle = nomSalleField.getText();
            int capacite = Integer.parseInt(capaciteField.getText());
            String equipement = equipementField.getText();
            String locationSalle = locationSalleField.getText();
            String qualite = qualiteComboBox.getValue();
            double prix = Double.parseDouble(prixField.getText());

            // Convertir la liste des chemins d'images en une seule chaîne (séparée par des virgules)
            String images = String.join(",", imagePaths);

            // Créer un nouvel objet Salle
            Salle salle = new Salle(
                    nomSalle, // nomSalle
                    capacite, // capacite
                    equipement, // equipement
                    images, // images (chemins séparés par des virgules)
                    locationSalle, // locationSalle
                    1, // userId (remplacez par l'ID de l'utilisateur connecté)
                    qualite, // qualite
                    prix // prix
            );

            // Ajouter la salle à la base de données
            salleService.ajouter(salle);

            // Fermer la fenêtre
            if (stage != null) {
                stage.close();
            }

            // Afficher un message de succès
            showAlert("Succès", "La salle a été ajoutée avec succès.");

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour la capacité et le prix.");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la salle : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelButton() {
        if (stage != null) {
            stage.close(); // Fermer la fenêtre sans rien faire
        }
    }

    @FXML
    private void handleChooseImageButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Ouvrir le dialogue de sélection de fichier
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Stocker le chemin de l'image
            imagePath = selectedFile.getAbsolutePath();
            // Afficher le chemin de l'image dans le label
            imagePathLabel.setText(selectedFile.getName());
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}