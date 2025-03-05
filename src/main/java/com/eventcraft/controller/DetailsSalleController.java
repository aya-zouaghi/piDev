package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.eventcraft.model.Salle;
import com.eventcraft.service.SalleService;
import com.eventcraft.service.ReservationSalleService;
import com.eventcraft.model.Reservationsalle;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private ImageView salleImageView;  // ImageView pour afficher l'image
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button afficherReservationsButton;
    @FXML private ImageView logoImageView;

    private User user;

    public void setUser(User user) {
        this.user = user;
        // Additional logic to handle the user object
    }
    @FXML
    private void openChatWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/chatbot.fxml"));
            Parent root = loader.load();
            Stage chatStage = new Stage();
            chatStage.setTitle("Assistance AI");
            chatStage.setScene(new Scene(root));
            chatStage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la fenêtre de chat : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private int currentSalleId;
    private int currentUserId;
    private final SalleService salleService = new SalleService();
    private final ReservationSalleService reservationSalleService = new ReservationSalleService();

    private Set<LocalDate> reservedDates = new HashSet<>();

    @FXML
    private AnchorPane coverImageContainer;  // Conteneur d'image (AnchorPane)

    private static final double IMAGE_WIDTH_RATIO = 1.0;  // Ajusté pour une meilleure proportion
    private static final double IMAGE_HEIGHT_RATIO = 1.0;
    private static final double MIN_IMAGE_WIDTH = 1000;  // Augmenté la largeur minimale
    @FXML
    private void handleSallesNavigation(ActionEvent event) {
        navigateToPage("profilsalle.fxml", event, "Gestion des salles");
    }
    @FXML
    private void initialize() {
        loadLogoImage();

        // Lorsque la largeur du conteneur change, ajuster la largeur de l'image
        coverImageContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Ajuster la largeur de l'image
            double newWidth = Math.max(newVal.doubleValue(), MIN_IMAGE_WIDTH);  // Assurez-vous que l'image ne soit pas plus petite que MIN_IMAGE_WIDTH
            salleImageView.setFitWidth(newWidth);  // L'image occupe toute la largeur mais respecte la largeur minimale

            // Calculer la hauteur en fonction de la largeur et du ratio (2.0:1)
            double newHeight = newWidth / IMAGE_WIDTH_RATIO * IMAGE_HEIGHT_RATIO;
            salleImageView.setFitHeight(newHeight);  // Ajuster la hauteur en fonction du ratio
            salleImageView.setPreserveRatio(true);  // L'image garde ses proportions
        });

        // Initialiser la date du DatePicker
        datePicker.setValue(LocalDate.now());

        // Charger les dates réservées
        try {
            reservedDates = reservationSalleService.getReservedDatesForSalle(currentSalleId);
            datePicker.setDayCellFactory(dayCell -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (reservedDates.contains(date)) {
                        setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
                        setDisable(true);
                    }
                }
            });
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des dates réservées : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLogoImage() {
        try {
            String imagePath = "/images/logoblanc.png";
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                System.err.println("L'image n'a pas été trouvée : " + imagePath);
                return;
            }
            Image logoImage = new Image(imageUrl.toString());
            logoImageView.setImage(logoImage);
            logoImageView.setFitWidth(120);
            logoImageView.setFitHeight(120);
            logoImageView.setPreserveRatio(true);
            VBox.setMargin(logoImageView, new Insets(-30, 0, 0, 0));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour initialiser les détails de la salle
    public void initializeDetails(int salleId) {
        try {
            Salle salle = salleService.getById(salleId);
            currentSalleId = salle.getIdSalle();

            salleNameLabel.setText(salle.getNomSalle());
            salleLocationLabel.setText("📍 " + salle.getLocationSalle());
            salleCapacityLabel.setText("👥 Capacité : " + salle.getCapacite());
            sallePriceLabel.setText("💰 Prix : " + salle.getPrix() + " DT");

            // Charger l'image de couverture
            String imageUrlsString = salle.getImageSalle();
            if (imageUrlsString != null && !imageUrlsString.isEmpty()) {
                String[] imageUrls = imageUrlsString.split(",");
                if (imageUrls.length > 0) {
                    String firstImagePath = imageUrls[0].trim();
                    File imageFile = new File(firstImagePath);
                    if (imageFile.exists()) {
                        // Charger l'image dans l'ImageView
                        salleImageView.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        System.err.println("Image introuvable : " + firstImagePath);
                    }
                }
            } else {
                System.err.println("Aucune image disponible pour cette salle.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des détails de la salle : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour afficher les réservations
    @FXML
    private void handleAfficherReservationsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservation.fxml"));
            Parent root = loader.load();

            ReservationSalleController reservationController = loader.getController();
            reservationController.afficherToutesReservations();

            Scene scene = new Scene(root);
            Stage stage = (Stage) afficherReservationsButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page des réservations.");
        }
    }

    // Méthode pour la gestion de la sélection de la date
    @FXML
    private void handleDateSelection() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null && !reservedDates.contains(selectedDate)) {
            System.out.println("Salle réservée pour la date: " + selectedDate);
        } else {
            System.out.println("Cette date est déjà réservée.");
        }
    }

    // Méthode pour réserver une salle
    @FXML
    private void handleReserverButton() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Non connecté", "Vous devez être connecté pour réserver une salle.");
            redirectToLoginPage();
            return;
        }

        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            showAlert("Erreur", "Veuillez sélectionner une date de réservation.");
            return;
        }

        // Vérifier si la date est déjà réservée
        if (reservedDates.contains(selectedDate)) {
            showAlert("Erreur", "La date sélectionnée est déjà réservée.");
            return;
        }

        // Créer une nouvelle réservation
        Reservationsalle reservation = new Reservationsalle();
        reservation.setSalle(currentSalleId);
        reservation.setUser_id(currentUser.getIdUser());
        reservation.setDate_debut(Date.valueOf(selectedDate));
        reservation.setDate_fin(Date.valueOf(selectedDate)); // Si la réservation est pour une seule journée

        try {
            reservationSalleService.ajouter(reservation);
            showAlert("Succès", "La réservation a été enregistrée avec succès.");
            // Mettre à jour les dates réservées
            reservedDates.add(selectedDate);
            datePicker.setDayCellFactory(dayCell -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (reservedDates.contains(date)) {
                        setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
                        setDisable(true);
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'enregistrement de la réservation.");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour rediriger vers la page de connexion
    private void redirectToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Connexion");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            Stage currentStage = (Stage) datePicker.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de connexion.");
        }
    }

    // Autres méthodes pour gérer les boutons de navigation
    @FXML
    private void handleAccueilButton() {
        System.out.println("Accueil button clicked");
    }

    @FXML
    private void handleEvenementsButton() {
        System.out.println("Evenements button clicked");
    }

    @FXML
    private void handlecontactButton() {
        System.out.println("Contact button clicked");
    }


    @FXML
    private void handleConnexionButton() {
        System.out.println("Connexion button clicked");
    }


    private void navigateToPage(String fxmlFile, ActionEvent event, String title) {
        try {
            // Construct the URL for the FXML file
            URL resourceUrl = getClass().getResource("/view/" + fxmlFile);

            if (resourceUrl == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            // Pass the user object to the new controller if it implements UserAwareController
            Object controller = loader.getController();
            if (controller instanceof UserAwareController && user != null) {
                ((UserAwareController) controller).setUser(user);
            }

            // Get the current stage and update the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("EventCraft - " + title);

            // Obtenir les dimensions de l'écran
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            // Définir la taille de la fenêtre pour qu'elle corresponde aux dimensions de l'écran
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            // Définir la scène et afficher la fenêtre
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading page " + title + ": " + e.getMessage());
        }
    }    private void navigateToWithUserData(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();

            // Set user data in the new controller
            if (fxmlFile.equals("/view/view_reclamation.fxml")) {
                ViewReclamationController viewReclamationController = loader.getController();
                viewReclamationController.setUser(user);
            } else if (fxmlFile.equals("/view/view_responses.fxml")) {
                ViewResponsesController viewResponsesController = loader.getController();
                viewResponsesController.setUser(user);
            } else if (fxmlFile.equals("/view/profile.fxml")) {
                ProfileController profileController = loader.getController();
                profileController.setUserData(user);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(page));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void showError(String message) {
        // You could implement this to show a dialog or alert
        System.err.println(message);
    }
    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();

        // Delete the tokens directory
        Path tokensDirectory = Paths.get("C:\\Users\\Baha Ayadi\\Desktop\\event_craft\\tokens");
        try {
            deleteDirectory(tokensDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to delete tokens directory: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        // Navigate to the login page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent loginPage = loader.load();
            Scene loginScene = new Scene(loginPage);
            // Load the CSS
            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            loginScene.getStylesheets().add(css);

            // Use a node that is guaranteed to be initialized
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to navigate to login page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            // Charger la page précédente (par exemple, la page des salles)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profilsalle.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur au contrôleur de la page précédente si nécessaire
            Object controller = loader.getController();
            if (controller instanceof UserAwareController && user != null) {
                ((UserAwareController) controller).setUser(user);
            }

            // Obtenir la scène actuelle et la fenêtre
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Définir la nouvelle scène et afficher la fenêtre
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir à la page précédente : " + e.getMessage());
        }
    }

}