package org.example.controller;

import SERVICE.ReservationSalleService;
import SERVICE.SalleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import org.example.entities.Salle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class HelloSalleController{
private ReservationSalleService reservationService = new ReservationSalleService(); // Initialisation du service

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
public void initialize() {
    mainContent.setTranslateX(0); // Le contenu prend toute la largeur
    System.out.println("Controller initialized!");

    // Charger les salles depuis la base de données
    loadSallesFromDatabase();

    sidenav.setTranslateX(-180); // Cache la navbar en la décalant hors de l'écran
}

// Action des boutons de la barre de menu
@FXML
private void handleAccueilButton() {
    System.out.println("Accueil button clicked");
}

@FXML
private void handleEvenementsButton() {
    System.out.println("Evenements button clicked");
}

@FXML
private void handleSallesButton() {
    System.out.println("Salles button clicked");
}

@FXML
private void handleBlogButton() {
    System.out.println("Blog button clicked");
}

@FXML
private void handlecontactButton() {
    System.out.println("Contact button clicked");
}

@FXML
private void handleInscriptionButton() {
    System.out.println("Inscription button clicked");
}

@FXML
private void handleConnexionButton() {
    System.out.println("Connexion button clicked");
}

@FXML
private void onSearch() {
    String query = searchField.getText();
    System.out.println("Recherche effectuée : " + query);
}

@FXML
private Button afficherReservationsButton;

@FXML
public void handleReserverClick() {
    System.out.println("Le bouton Réserver a été cliqué !");
}


@FXML
private void handleAjouterSalleButton() {
    try {
        URL fxmlLocation = getClass().getResource("/view/ajoutSalle.fxml");
        if (fxmlLocation == null) {
            System.err.println("Fichier FXML introuvable : /view/ajoutSalle.fxml");
            return;
        }
        System.out.println("Chargement du fichier FXML : " + fxmlLocation);

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Récupérer le contrôleur
        AddSalleController controller = loader.getController();

        // Créer une nouvelle fenêtre
        Stage ajoutStage = new Stage();
        ajoutStage.setTitle("Ajouter une Salle");
        ajoutStage.setScene(new Scene(root));

        // Passer la référence de la fenêtre au contrôleur
        controller.setStage(ajoutStage);

        // Afficher la fenêtre
        ajoutStage.show();
    } catch (IOException e) {
        System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
        e.printStackTrace();
    }
}

@FXML
private void openNav() {
    double targetNavX = isNavOpen ? -180 : 0; // Navbar : cachée (-180) ou visible (0)
    double targetContentX = isNavOpen ? 0 : 50; // Contenu : normal (0) ou décalé (50)

    // Transition pour la navbar
    TranslateTransition navTransition = new TranslateTransition(Duration.millis(300), sidenav);
    navTransition.setToX(targetNavX);

    // Transition pour le contenu principal
    TranslateTransition contentTransition = new TranslateTransition(Duration.millis(300), mainContent);
    contentTransition.setToX(targetContentX);

    // Lancer les transitions en même temps
    navTransition.play();
    contentTransition.play();

    isNavOpen = !isNavOpen; // Alterner l'état
}

/**
 * Charge les salles depuis la base de données.
 */
private void loadSallesFromDatabase() {
    try {
        // Charger toutes les salles sans filtre sur la disponibilité
        List<Salle> sallesFromDB = salleService.afficher();
        ObservableList<Salle> salles = FXCollections.observableArrayList(sallesFromDB);
        listViewSalles.setItems(salles);

        // Définir l'affichage personnalisé des cellules
        listViewSalles.setCellFactory(param -> new ListCell<Salle>() {
            private final HBox hbox = new HBox(10); // Conteneur principal pour l'image, les labels et les boutons
            private final ImageView imageView = new ImageView(); // Pour afficher l'image
            private final VBox infoBox = new VBox(5); // Pour le titre, la localisation et le prix
            private final Label nameLabel = new Label(); // Titre de la salle
            private final Label detailsLabel = new Label(); // Localisation et capacité
            private final Label priceLabel = new Label(); // Prix
            private final HBox buttonBox = new HBox(10); // Pour les boutons
            private final Button reserverButton = new Button("Réserver"); // Bouton Réserver
            private final Button detButton = new Button("Voir Détail"); // Bouton Voir Détails

            {
                // Style et configuration des éléments
                hbox.setAlignment(Pos.CENTER_LEFT); // Aligner les éléments à gauche
                hbox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ddd;");

                // Configuration de l'image
                imageView.setFitWidth(100); // Largeur de l'image
                imageView.setFitHeight(100); // Hauteur de l'image
                imageView.setPreserveRatio(true); // Conserver le ratio de l'image

                // Configuration des labels
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                detailsLabel.setStyle("-fx-font-size: 14px;");
                priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50;");

                // Configuration des boutons
                reserverButton.setStyle("-fx-background-color: #ff6600; -fx-text-fill: white;");
                detButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                // Ajouter les éléments à la VBox (infoBox)
                infoBox.getChildren().addAll(nameLabel, detailsLabel, priceLabel);

                // Ajouter les boutons à la HBox (buttonBox)
                buttonBox.getChildren().addAll(reserverButton, detButton);
                buttonBox.setAlignment(Pos.CENTER_RIGHT); // Aligner les boutons à droite

                // Ajouter l'image, les infos et les boutons à la HBox principale
                hbox.getChildren().addAll(imageView, infoBox, buttonBox);

                // Actions des boutons
                reserverButton.setOnAction(event -> {
                    Salle selectedSalle = getItem(); // Récupérer la salle associée à cette cellule
                    if (selectedSalle != null) {
                        handleReserverButton(selectedSalle); // Ouvrir le formulaire de réservation
                    }
                });

                detButton.setOnAction(e -> {
                    Salle selectedSalle = getItem(); // Récupérer la salle associée à cette cellule
                    if (selectedSalle != null) {
                        openDetailsWindow(selectedSalle); // Ouvrir la fenêtre des détails
                    }
                });
            }

            @Override
            protected void updateItem(Salle salle, boolean empty) {
                super.updateItem(salle, empty);

                if (empty || salle == null) {
                    setGraphic(null); // Masquer la cellule si elle est vide
                } else {
                    // Mettre à jour les informations de la salle
                    nameLabel.setText(salle.getNomSalle());
                    detailsLabel.setText("📍 " + salle.getLocationSalle() + "  |  👥 " + salle.getCapacite());
                    priceLabel.setText("💰 " + salle.getPrix() + " DT");

                    // Charger la première image de la salle
                    String imageUrls = salle.getImageSalle(); // Récupérer la chaîne des chemins d'images
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        // Extraire la première image (séparée par des virgules)
                        String[] images = imageUrls.split(",");
                        String firstImageUrl = images[0].trim(); // Prendre la première image

                        try {
                            Image image = new Image(new File(firstImageUrl).toURI().toString());
                            imageView.setImage(image);
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                        }
                    }

                    // Afficher la HBox principale
                    setGraphic(hbox);
                }
            }
        });

    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Erreur lors du chargement des salles : " + e.getMessage());
    }
}

@FXML
private void handleReserverButton(Salle selectedSalle) {
    try {
        // Charger le fichier FXML du formulaire de réservation
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservationForm.fxml"));
        Parent root = loader.load();

        // Obtenir le contrôleur et définir les informations nécessaires
        ReservationFormController controller = loader.getController();
        controller.setSalleId(selectedSalle.getIdSalle());
        controller.setUserId(1); // Remplacez par l'ID de l'utilisateur connecté
        controller.setStage(new Stage());

        // Créer une nouvelle fenêtre pour le formulaire
        Stage reservationStage = new Stage();
        reservationStage.setTitle("Formulaire de Réservation");
        reservationStage.setScene(new Scene(root));
        reservationStage.show();
    } catch (IOException e) {
        e.printStackTrace();
        System.err.println("Erreur lors du chargement du formulaire de réservation : " + e.getMessage());
    }
}

@FXML
private void reserverButton(Salle selectedSalle) {
    try {
        // Charger le fichier FXML de la page des réservations
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservation.fxml"));
        Parent root = loader.load();

        // Obtenir le contrôleur de la page des réservations
        ReservationSalleController reservationController = loader.getController();

        // Créer une nouvelle fenêtre pour afficher les réservations
        Stage reservationStage = new Stage();
        reservationStage.setTitle("Réservations");
        reservationStage.setScene(new Scene(root));
        reservationStage.show();
    } catch (IOException e) {
        e.printStackTrace();
        System.err.println("Erreur lors du chargement de la page des réservations : " + e.getMessage());
    }
}

/**
 * Ouvre la fenêtre des détails de la salle.
 */
private void openDetailsWindow(Salle salle) {
    try {
        // Charger le fichier FXML
        URL fxmlLocation = getClass().getResource("/view/detailsalle.fxml");
        if (fxmlLocation == null) {
            System.err.println("Fichier FXML introuvable : /view/detailsalle.fxml");
            return;
        }
        System.out.println("Chargement du fichier FXML : " + fxmlLocation);

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Récupérer le contrôleur
        DetailsSalleController controller = loader.getController();

        // Initialiser les détails de la salle avec l'ID
        controller.initializeDetails(salle.getIdSalle()); // Passer l'ID de la salle sélectionnée

        // Créer une nouvelle fenêtre
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Détails de la Salle");
        detailsStage.setScene(new Scene(root));
        detailsStage.show();

    } catch (IOException e) {
        System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
        e.printStackTrace();
    }
}
}