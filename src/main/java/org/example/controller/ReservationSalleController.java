package org.example.controller;

import SERVICE.ReservationSalleService;
import SERVICE.SalleService;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.entities.Reservationsalle;
import org.example.entities.Salle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ReservationSalleController {

    private ReservationSalleService reservationService = new ReservationSalleService();
    private SalleService salleService = new SalleService();

    @FXML
    private Button afficherReservationsButton;

    @FXML
    private TextField searchField;

    @FXML
    private VBox sidenav;

    @FXML
    private VBox mainContent;

    @FXML
    private ListView<HBox> listViewReservations; // Changé en ListView<HBox> pour afficher des éléments personnalisés

    private boolean isNavOpen = false;

    @FXML
    public void initialize() {
        // Définir la CellFactory personnalisée
        listViewReservations.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });

        // Charger les réservations
        afficherToutesReservations();
    }

    // Méthode pour afficher toutes les réservations
    public void afficherToutesReservations() {
        try {
            List<Reservationsalle> reservations = reservationService.afficher();
            listViewReservations.getItems().clear();

            for (Reservationsalle reservation : reservations) {
                Salle salle = salleService.getById(reservation.getSalle());

                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ddd;");

                ImageView imageView = new ImageView();
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);

                String imagePath = salle.getImageSalle();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        Image image = new Image(new File(imagePath).toURI().toString());
                        imageView.setImage(image);
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                    }
                }

                VBox infoBox = new VBox(5);
                Label nameLabel = new Label("Salle: " + salle.getNomSalle());
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                Label datesLabel = new Label("Du " + reservation.getDate_debut() + " au " + reservation.getDate_fin());
                Label priceLabel = new Label("💰 " + salle.getPrix() + " DT");

                infoBox.getChildren().addAll(nameLabel, datesLabel, priceLabel);

                HBox buttonBox = new HBox(10);
                Button modifierButton = new Button("Modifier");
                Button supprimerButton = new Button("Supprimer");
                Button deviButton = new Button("voir devis");


                modifierButton.setOnAction(event -> modifierReservationPopup(reservation));

                supprimerButton.setOnAction(event -> supprimerReservation(reservation.getId_reservation()));
                deviButton.setOnAction(event -> modifierReservationPopup(reservation));

                buttonBox.getChildren().addAll(modifierButton, supprimerButton, deviButton);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);

                hbox.getChildren().addAll(imageView, infoBox, buttonBox);
                listViewReservations.getItems().add(hbox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void modifierReservationPopup(Reservationsalle reservation) {
        // Créer une boîte de dialogue personnalisée
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Réservation");
        dialog.setHeaderText("Modifier les dates de la réservation");

        // Créer les champs de saisie pour les dates
        DatePicker dateDebutPicker = new DatePicker();
        DatePicker dateFinPicker = new DatePicker();

        // Initialiser les DatePicker avec les dates actuelles de la réservation
        dateDebutPicker.setValue(reservation.getDate_debut().toLocalDate());
        dateFinPicker.setValue(reservation.getDate_fin().toLocalDate());

        // Créer un layout pour organiser les champs
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Label("Nouvelle date de début :"), dateDebutPicker,
                new Label("Nouvelle date de fin :"), dateFinPicker
        );

        // Ajouter les boutons OK et Annuler
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Gérer la réponse de l'utilisateur
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Récupérer les nouvelles dates
                    Date newDateDebut = Date.valueOf(dateDebutPicker.getValue());
                    Date newDateFin = Date.valueOf(dateFinPicker.getValue());

                    // Vérifier que la date de fin est postérieure à la date de début
                    if (newDateFin.before(newDateDebut)) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "La date de fin doit être postérieure à la date de début !");
                        alert.showAndWait();
                        return;
                    }

                    // Mettre à jour la réservation
                    reservation.setDate_debut(newDateDebut);
                    reservation.setDate_fin(newDateFin);

                    // Appeler le service pour modifier la réservation
                    reservationService.modifier(reservation);

                    // Rafraîchir la liste des réservations
                    afficherToutesReservations();

                    // Afficher un message de succès
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Réservation modifiée avec succès !");
                    successAlert.showAndWait();
                } catch (IllegalArgumentException e) {
                    // Gérer les erreurs de format de date
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Format de date invalide !");
                    alert.showAndWait();
                } catch (SQLException e) {
                    // Gérer les erreurs SQL
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification de la réservation.");
                    alert.showAndWait();
                }
            }
        });
    }

    private void supprimerReservation(int id_reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette réservation ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    reservationService.supprimer(id_reservation);
                    afficherToutesReservations();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // Méthode pour la transition du menu latéral
    @FXML
    private void openNav() {
        double targetNavX = isNavOpen ? -180 : 0; // Navbar cachée (-180) ou visible (0)
        double targetContentX = isNavOpen ? 0 : 50; // Contenu normal (0) ou décalé (50)

        TranslateTransition navTransition = new TranslateTransition(Duration.millis(300), sidenav);
        navTransition.setToX(targetNavX);

        TranslateTransition contentTransition = new TranslateTransition(Duration.millis(300), mainContent);
        contentTransition.setToX(targetContentX);

        navTransition.play();
        contentTransition.play();

        isNavOpen = !isNavOpen;
    }

    // Autres méthodes existantes (ajouter, modifier, supprimer, etc.)
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

    // Autres méthodes existantes (ajouter, modifier, supprimer, etc.)
    public void ajouterReservation(int salle, int user_id, Date date_debut, Date date_fin) {
        Reservationsalle reservation = new Reservationsalle(0, salle, user_id, date_debut, date_fin);
        try {
            reservationService.ajouter(reservation);
            System.out.println("Réservation ajoutée avec succès : " + reservation);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout de la réservation.");
        }
    }

    public void modifierReservation(int id_reservation, int salle, int user_id, Date date_debut, Date date_fin) {
        Reservationsalle reservation = new Reservationsalle(id_reservation, salle, user_id, date_debut, date_fin);
        try {
            reservationService.modifier(reservation);
            System.out.println("Réservation modifiée avec succès : " + reservation);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la modification de la réservation.");
        }
    }



    @FXML
    private void handlecontactButton() {
        System.out.println("Contact button clicked");
    }

    @FXML
    private void handleConnexionButton() {
        System.out.println("Connexion button clicked");
    }

    @FXML
    private void handleAccueilButton() {
        System.out.println("Accueil button clicked");
    }

    @FXML
    private void handleEvenementsButton() {
        System.out.println("Evenements button clicked");
    }


    private void confirmerSuppression(Reservationsalle reservation) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Voulez-vous vraiment supprimer cette réservation ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                supprimerReservation(reservation.getId_reservation());
            }
        });
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

            // Récupérer le contrôleur du FXML chargé
            AddSalleController controller = loader.getController();

            // Créer et afficher une nouvelle fenêtre
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

}