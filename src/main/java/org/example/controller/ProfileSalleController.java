package org.example.controller;

import SERVICE.ReservationSalleService;
import SERVICE.SalleService;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.entities.Reservationsalle;
import org.example.entities.Salle;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProfileSalleController{

@FXML
private ListView<Salle> listViewSalles;

@FXML
private HBox menuBar;

@FXML
private TextField searchField;

@FXML
private VBox sidenav;

@FXML
private VBox mainContent;

private boolean isNavOpen = false;

private final SalleService salleService = new SalleService();

    @FXML
    private Button afficherReservationsButton;

@FXML
public void initialize() {
    mainContent.setTranslateX(0);
    System.out.println("Controller initialized!");

    // Charger les salles depuis la base de données
    loadSallesFromDatabase();

    sidenav.setTranslateX(-180);
}

@FXML
private void handleAccueilButton() {
    System.out.println("Accueil button clicked");
    // Action pour le bouton Accueil
}

@FXML
private void handleEvenementsButton() {
    System.out.println("Evenements button clicked");
    loadSallesFromDatabase();
}

@FXML
private void handleSallesButton() {
    System.out.println("Salles button clicked");
    // Action pour le bouton Salles
}

@FXML
private void handleBlogButton() {
    System.out.println("Blog button clicked");
    // Action pour le bouton Blog
}

@FXML
private void handlecontactButton() {
    System.out.println("Contact button clicked");
    // Action pour le bouton Contact
}

@FXML
private void handleInscriptionButton() {
    System.out.println("Inscription button clicked");
    // Action pour le bouton Inscription
}

@FXML
private void handleConnexionButton() {
    System.out.println("Connexion button clicked");
    // Action pour le bouton Connexion
}

@FXML
private void onSearch() {
    String query = searchField.getText();
    System.out.println("Recherche effectuée : " + query);
    // Logique pour traiter la recherche
}

@FXML
private void handleAjouterSalleButton() {
    try {
        URL fxmlLocation = getClass().getResource("/view/ajoutSalle.fxml");
        if (fxmlLocation == null) {
            System.err.println("Fichier FXML introuvable : /view/ajoutSalle.fxml");
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        AddSalleController controller = loader.getController();
        Stage ajoutStage = new Stage();
        ajoutStage.setTitle("Ajouter une Salle");
        ajoutStage.setScene(new Scene(root));
        controller.setStage(ajoutStage);
        ajoutStage.show();

    } catch (IOException e) {
        System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
        e.printStackTrace();
    }
}

@FXML
private void openNav() {
    double targetNavX = isNavOpen ? -180 : 0;
    double targetContentX = isNavOpen ? 0 : 50;

    TranslateTransition navTransition = new TranslateTransition(Duration.millis(300), sidenav);
    navTransition.setToX(targetNavX);

    TranslateTransition contentTransition = new TranslateTransition(Duration.millis(300), mainContent);
    contentTransition.setToX(targetContentX);

    navTransition.play();
    contentTransition.play();

    isNavOpen = !isNavOpen;
}

private void loadSallesFromDatabase() {
    try {
        List<Salle> sallesFromDB = salleService.afficher();
        ObservableList<Salle> salles = FXCollections.observableArrayList(sallesFromDB);
        listViewSalles.setItems(salles);

        listViewSalles.setCellFactory(param -> new ListCell<Salle>() {
            private final HBox hbox = new HBox(10);
            private final ImageView imageView = new ImageView();
            private final VBox infoBox = new VBox(5);
            private final Label nameLabel = new Label();
            private final Label detailsLabel = new Label();
            private final Label priceLabel = new Label();
            private final HBox buttonBox = new HBox(10);
            private final Button modifierButton = new Button("Modifier");
            private final Button annulerButton = new Button("Annuler");

            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ddd;");

                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);

                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                detailsLabel.setStyle("-fx-font-size: 14px;");
                priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50;");

                modifierButton.setStyle("-fx-background-color: #ff6600; -fx-text-fill: white;");
                annulerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                infoBox.getChildren().addAll(nameLabel, detailsLabel, priceLabel);
                buttonBox.getChildren().addAll(modifierButton, annulerButton);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);

                hbox.getChildren().addAll(imageView, infoBox, buttonBox);

                // Action pour le bouton "Modifier"
                modifierButton.setOnAction(event -> {
                    Salle selectedSalle = getItem();
                    if (selectedSalle != null) {
                        handleModifierButton(selectedSalle);
                    }
                });

                // Action pour le bouton "Supprimer"
                annulerButton.setOnAction(event -> {
                    Salle selectedSalle = getItem();
                    if (selectedSalle != null) {
                        handleSupprimerButton(selectedSalle);
                    }
                });
            }

            @Override
            protected void updateItem(Salle salle, boolean empty) {
                super.updateItem(salle, empty);

                if (empty || salle == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(salle.getNomSalle());
                    detailsLabel.setText("📍 " + salle.getLocationSalle() + "  |  👥 " + salle.getCapacite());
                    priceLabel.setText("💰 " + salle.getPrix() + " DT");

                    String imageUrls = salle.getImageSalle();
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        String[] images = imageUrls.split(",");
                        String firstImageUrl = images[0].trim();

                        try {
                            Image image = new Image(new File(firstImageUrl).toURI().toString());
                            imageView.setImage(image);
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                        }
                    }

                    setGraphic(hbox);
                }
            }
        });

    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Erreur lors du chargement des salles : " + e.getMessage());
    }
}

    private void handleModifierButton(Salle selectedSalle) {
        // Créer une boîte de dialogue pour modifier la salle
        Dialog<Salle> dialog = new Dialog<>();
        dialog.setTitle("Modifier une Salle");
        dialog.setHeaderText("Modifier les détails de la salle");

        // Ajouter les champs de saisie
        TextField nomSalleField = new TextField(selectedSalle.getNomSalle());
        TextField capaciteField = new TextField(String.valueOf(selectedSalle.getCapacite()));
        TextField equipementField = new TextField(selectedSalle.getEquipement());
        TextField imageSalleField = new TextField(selectedSalle.getImageSalle());
        TextField locationSalleField = new TextField(selectedSalle.getLocationSalle());

        // Créer une ComboBox pour la qualité avec les nouvelles options
        ComboBox<String> qualiteComboBox = new ComboBox<>();
        qualiteComboBox.getItems().addAll("Fabuleux", "Très bien", "Exceptionnel", "Superbe"); // Nouvelle liste de qualités
        qualiteComboBox.setValue(selectedSalle.getQualite()); // Sélectionner la qualité actuelle

        TextField prixField = new TextField(String.valueOf(selectedSalle.getPrix()));

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Nom de la salle:"), nomSalleField,
                new Label("Capacité:"), capaciteField,
                new Label("Équipement:"), equipementField,
                new Label("Image de la salle:"), imageSalleField,
                new Label("Localisation:"), locationSalleField,
                new Label("Qualité:"), qualiteComboBox, // Utiliser la ComboBox ici
                new Label("Prix:"), prixField
        ));

        // Ajouter les boutons OK et Annuler
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convertir le résultat en objet Salle
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                selectedSalle.setNomSalle(nomSalleField.getText());
                selectedSalle.setCapacite(Integer.parseInt(capaciteField.getText()));
                selectedSalle.setEquipement(equipementField.getText());
                selectedSalle.setImageSalle(imageSalleField.getText());
                selectedSalle.setLocationSalle(locationSalleField.getText());
                selectedSalle.setQualite(qualiteComboBox.getValue()); // Récupérer la valeur de la ComboBox
                selectedSalle.setPrix(Double.parseDouble(prixField.getText()));
                return selectedSalle;
            }
            return null;
        });

        // Afficher la boîte de dialogue et traiter le résultat
        Optional<Salle> result = dialog.showAndWait();
        result.ifPresent(salle -> {
            try {
                salleService.modifier(salle); // Mettre à jour la salle dans la base de données
                loadSallesFromDatabase(); // Recharger la liste des salles
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de la modification de la salle : " + e.getMessage());
            }
        });
    }

private void handleSupprimerButton(Salle selectedSalle) {
    try {
        // Supprimer la salle de la base de données
        salleService.supprimer(selectedSalle.getIdSalle());

        // Recharger la liste des salles après la suppression
        loadSallesFromDatabase();

        // Afficher un message de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Suppression réussie");
        alert.setHeaderText(null);
        alert.setContentText("La salle a été supprimée avec succès.");
        alert.showAndWait();

    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Erreur lors de la suppression de la salle : " + e.getMessage());

        // Afficher un message d'erreur
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Une erreur s'est produite lors de la suppression de la salle.");
        alert.showAndWait();
    }
}



    @FXML
    private void handleAfficherReservationsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservation.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la scène de réservation
            ReservationSalleController reservationController = loader.getController();

            // Passer les données nécessaires au contrôleur
            reservationController.afficherToutesReservations();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle et définir la nouvelle scène
            Stage stage = (Stage) afficherReservationsButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la page des réservations");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page. Veuillez réessayer.");
            alert.showAndWait();
        }
    }
}
