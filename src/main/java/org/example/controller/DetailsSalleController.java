package org.example.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.entities.Salle;
import SERVICE.SalleService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DetailsSalleController {

    @FXML
    private Label salleNameLabel;
    @FXML
    private Label salleLocationLabel;
    @FXML
    private Label salleCapacityLabel;
    @FXML
    private Label sallePriceLabel;
    @FXML
    private ImageView mainImageView;
    @FXML
    private Label captionLabel;
    @FXML
    private HBox thumbnailContainer;
    @FXML
    private StackPane slideshowContainer;
    @FXML
    private ImageView salleImageView;
    @FXML
    private VBox sidenav;
    @FXML
    private VBox mainContent;

    private int currentSalleId;
    private int currentUserId;
    private boolean isNavOpen = false;
    private final SalleService salleService = new SalleService();
    private List<String> imageUrls;
    private int currentImageIndex = 0;



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

    @FXML
    public void initialize() {
        mainContent.setTranslateX(0); // Le contenu prend toute la largeur
        System.out.println("Controller initialized!");
        sidenav.setTranslateX(-180); // Cache la navbar en la décalant hors de l'écran
    }

    // Cette méthode va récupérer les détails de la salle par son ID et les afficher.
    public void initializeDetails(int salleId) {
        try {
            // Récupérer les détails de la salle depuis la base de données
            Salle salle = salleService.getById(salleId);

            // Stocker l'ID de la salle
            currentSalleId = salle.getIdSalle();  // Assurez-vous que getIdSalle() retourne l'ID de la salle

            // Mettre à jour les éléments de l'interface avec les détails de la salle
            salleNameLabel.setText(salle.getNomSalle());
            salleLocationLabel.setText("📍 " + salle.getLocationSalle());
            salleCapacityLabel.setText("👥 Capacité : " + salle.getCapacite());
            sallePriceLabel.setText("💰 Prix : " + salle.getPrix() + " DT");

            // Charger l'image de la salle
            String imageUrl = salle.getImageSalle();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Image image = new Image(new File(imageUrl).toURI().toString());
                salleImageView.setImage(image);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des détails de la salle : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour afficher une image du diaporama
    private void showImage(int index) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String imageUrl = imageUrls.get(index);
            Image image = new Image(new File(imageUrl).toURI().toString());
            mainImageView.setImage(image);
            captionLabel.setText("Image " + (index + 1) + " de " + imageUrls.size());
        }
    }

    // Méthode pour passer à l'image suivante
    private void nextImage() {
        currentImageIndex = (currentImageIndex + 1) % imageUrls.size();
        showImage(currentImageIndex);
    }



    // Méthode pour ouvrir le formulaire de réservation en tant que fenêtre modale
    @FXML
    private void handleReserverButton() {
        try {
            // Charger le fichier FXML du formulaire de réservation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservationForm.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur du formulaire
            ReservationFormController controller = loader.getController();

            // Passer l'ID de la salle actuelle au formulaire de réservation
            controller.setSalleId(currentSalleId);  // Utilisez la variable currentSalleId
            controller.setUserId(currentUserId);    // Remplacez currentUserId par l'ID de l'utilisateur connecté

            // Créer et afficher la fenêtre modale
            Stage reservationStage = new Stage();
            reservationStage.setTitle("Formulaire de Réservation");
            reservationStage.setScene(new Scene(root));
            reservationStage.initOwner(salleNameLabel.getScene().getWindow()); // Fenêtre principale
            reservationStage.initModality(Modality.APPLICATION_MODAL); // Fenêtre modale
            reservationStage.show(); // Afficher la fenêtre modale

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du formulaire de réservation : " + e.getMessage());
        }
    }

}
