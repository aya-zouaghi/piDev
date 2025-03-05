package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.service.ReservationSalleService;
import com.eventcraft.service.SalleService;
import com.eventcraft.util.SessionManager;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.eventcraft.model.Reservationsalle;
import com.eventcraft.model.Salle;
import javafx.scene.text.Text;
import javafx.scene.Group;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import javafx.geometry.Rectangle2D;

import javafx.geometry.Insets;

public class ReservationSalleController {
    private static final int ITEMS_PER_PAGE = 5;

    private ReservationSalleService reservationService = new ReservationSalleService();
    private SalleService salleService = new SalleService();
    private int userId;

    @FXML private ListView<Salle> listViewSalles;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> capacityFilter;
    @FXML private ComboBox<String> qualityFilter;
    @FXML private TextField advancedPriceField;
    @FXML private Button afficherReservationsButton;
    @FXML private VBox sidenav;
    @FXML private VBox mainContent;
    @FXML private Pagination pagination;
    @FXML private ListView<HBox> listViewReservations;
    @FXML private ImageView logoImageView;
    private User user;

    public void setUser(User user) {
        this.user = user;
        // Additional logic to handle the user object
    }
    @FXML
    private void handleSallesNavigation(ActionEvent event) {
        navigateToPage("profilsalle.fxml", event, "Gestion des salles");
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
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading page " + title + ": " + e.getMessage());
        }
    }

    private void showError(String message) {
        // You could implement this to show a dialog or alert
        System.err.println(message);
    }
    @FXML
    public void initialize() {
        loadLogoImage();

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

        // Récupérer l'utilisateur connecté et définir userId
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            this.userId = currentUser.getIdUser();
        } else {
            System.out.println("Aucun utilisateur connecté.");
            return;
        }

        // Charger les réservations de l'utilisateur connecté
        afficherToutesReservations();

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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Méthode pour afficher toutes les réservations
    public void afficherToutesReservations() {
        try {
            // Vérifier que l'utilisateur est connecté
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                System.out.println("Aucun utilisateur connecté.");
                return;
            }

            // Utiliser l'ID de l'utilisateur connecté pour récupérer ses réservations
            List<Reservationsalle> reservations = reservationService.getReservationsByUserId(currentUser.getIdUser());
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
                Button deviButton = new Button("Voir Devis");

                // Appliquer les styles CSS directement aux boutons
                modifierButton.setStyle(
                        "-fx-background-color: #8D598F; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-cursor: hand;"
                );

                supprimerButton.setStyle(
                        "-fx-background-color: #FF4444; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-cursor: hand;"
                );

                deviButton.setStyle(
                        "-fx-background-color: #8D598F; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-cursor: hand;"
                );

                // Ajouter des styles au survol
                modifierButton.setOnMouseEntered(e -> modifierButton.setStyle(
                        "-fx-background-color: #8D598F; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-border-radius: 5px; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-cursor: hand;"
                ));
                modifierButton.setOnMouseExited(e -> modifierButton.setStyle(
                        "-fx-background-color: #B19BB2; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-border-radius: 5px; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-cursor: hand;"
                ));

                supprimerButton.setOnMouseEntered(e -> supprimerButton.setStyle(
                        "-fx-background-color: #B19BB2; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-cursor: hand;"
                ));
                supprimerButton.setOnMouseExited(e -> supprimerButton.setStyle(
                        "-fx-background-color: #8D598F; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-cursor: hand;"
                ));

                deviButton.setOnMouseEntered(e -> deviButton.setStyle(
                        "-fx-background-color: #B19BB2; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-cursor: hand;"
                ));
                deviButton.setOnMouseExited(e -> deviButton.setStyle(
                        "-fx-background-color: #B19BB2; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-cursor: hand;"
                ));

                // Ajouter les actions
                modifierButton.setOnAction(event -> modifierReservationPopup(reservation));
                supprimerButton.setOnAction(event -> supprimerReservation(reservation.getId_reservation()));
                deviButton.setOnAction(event -> afficherDevis(reservation, salle));

                buttonBox.getChildren().addAll(modifierButton, supprimerButton, deviButton);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);

                hbox.getChildren().addAll(imageView, infoBox, buttonBox);
                listViewReservations.getItems().add(hbox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du chargement des réservations");
            alert.setContentText("Une erreur s'est produite lors du chargement des réservations. Veuillez réessayer.");
            alert.showAndWait();
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

    private void afficherDevis(Reservationsalle reservation, Salle salle) {
        Stage devisStage = new Stage();
        devisStage.setTitle("Devis de Réservation");

        // Créer le contenu de la fenêtre
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Devis de Réservation");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label salleLabel = new Label("Salle : " + salle.getNomSalle());
        Label dateLabel = new Label("Date : Du " + reservation.getDate_debut() + " au " + reservation.getDate_fin());
        Label capaciteLabel = new Label("Capacité : " + salle.getCapacite() + " DT");
        Label priceLabel = new Label("Prix : " + salle.getPrix() + " DT");

        Button deviButton = new Button("Imprimer Devis");
        vbox.getChildren().addAll(titleLabel, salleLabel, dateLabel, capaciteLabel, priceLabel, deviButton);
        deviButton.setOnAction(e -> imprimerDevis(reservation, salle));

        // Créer la scène
        Scene scene = new Scene(vbox);

        // Obtenir les dimensions de l'écran (en excluant la barre des tâches)
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        // Définir la taille et la position de la fenêtre
        devisStage.setX(bounds.getMinX()); // Position X en haut à gauche
        devisStage.setY(bounds.getMinY()); // Position Y en haut à gauche
        devisStage.setWidth(bounds.getWidth()); // Largeur de l'écran
        devisStage.setHeight(bounds.getHeight()); // Hauteur de l'écran

        // Définir la scène et afficher la fenêtre
        devisStage.setScene(scene);
        devisStage.show();
    }
    private void imprimerDevis(Reservationsalle reservation, Salle salle) {
        // Créer un PrinterJob
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(null)) {
            // Créer le contenu à imprimer sous forme de texte
            String devisContent = "Devis de Réservation\n\n" +
                    "Salle : " + salle.getNomSalle() + "\n" +
                    "Date : Du " + reservation.getDate_debut() + " au " + reservation.getDate_fin() + "\n" +
                    "Prix : " + salle.getPrix() + " DT\n";

            // Créer un objet Text avec le contenu du devis
            Text text = new Text(devisContent);

            // Créer un Group pour contenir le texte
            Group root = new Group();
            root.getChildren().add(text);

            // Imprimer le texte
            printerJob.printPage(root);
            printerJob.endJob();
        }
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

    @FXML
    private void handleAfficherReservationsClick() {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservation.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la scène de réservation
            ReservationSalleController reservationController = loader.getController();

            // Passer l'ID de l'utilisateur au contrôleur
            reservationController.setUserId(userId);

            // Rafraîchir la liste des réservations
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
            showAlert("Impossible de revenir à la page précédente : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
