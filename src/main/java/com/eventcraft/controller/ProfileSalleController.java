package com.eventcraft.controller;

import com.eventcraft.controller.AddSalleController;
import com.eventcraft.model.User;
import com.eventcraft.service.ReservationSalleService;
import com.eventcraft.service.SalleService;
import com.eventcraft.util.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.eventcraft.model.Salle;
import javafx.scene.text.Font;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProfileSalleController {
    private static final int ITEMS_PER_PAGE = 5;

    private ObservableList<Salle> allSalles = FXCollections.observableArrayList();
    private ObservableList<Salle> filteredSalles = FXCollections.observableArrayList();

    @FXML
    private ListView<Salle> listViewSalles;

    @FXML
    private HBox menuBar;

    @FXML
    private VBox sidenav;
    private User user;

    public void setUser(User user) {
        this.user = user;
        // Additional logic to handle the user object
    }
    @FXML
    private VBox mainContent;
    @FXML
    private TextField searchField;
    @FXML private ComboBox<String> capacityFilter;
    @FXML private ComboBox<String> qualityFilter;
    @FXML private TextField advancedPriceField;
    @FXML private ImageView logoImageView;

    private boolean isNavOpen = false;

    private final SalleService salleService = new SalleService();
    @FXML
    private void handleSallesNavigation(ActionEvent event) {
        navigateToPage("salle.fxml", event, "Gestion des salles");
    }
    @FXML
    private Button afficherReservationsButton;

    @FXML private Pagination pagination;

    @FXML
    public void initialize() {
        loadLogoImage();
        // Initialiser les filtres
        initializeFilters();
        setupPagination();

        // Charger les salles depuis la base de données
        loadSallesFromDatabase();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterSalles());
        capacityFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filterSalles());
        qualityFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filterSalles());
        advancedPriceField.textProperty().addListener((observable, oldValue, newValue) -> filterSalles());
    }

    private void setupPagination() {
        pagination.setPageCount(calculatePageCount());
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            updateListViewForCurrentPage();
        });
        updateListViewForCurrentPage();
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

    private int calculatePageCount() {
        return (int) Math.ceil((double) filteredSalles.size() / ITEMS_PER_PAGE);
    }

    private void updateListViewForCurrentPage() {
        int pageIndex = pagination.getCurrentPageIndex();
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredSalles.size());
        listViewSalles.setItems(FXCollections.observableArrayList(
                filteredSalles.subList(fromIndex, toIndex)
        ));
    }

    private void filterSalles() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCapacity = capacityFilter.getValue();
        String selectedQuality = qualityFilter.getValue();
        String maxPriceText = advancedPriceField.getText();

        try {
            filteredSalles.setAll(allSalles.stream()
                    .filter(salle -> salle.getNomSalle().toLowerCase().contains(searchText) ||
                            salle.getLocationSalle().toLowerCase().contains(searchText))
                    .filter(salle -> selectedCapacity.equals("Capacité") ||
                            String.valueOf(salle.getCapacite()).equals(selectedCapacity))
                    .filter(salle -> selectedQuality.equals("Qualité") ||
                            salle.getQualite().equals(selectedQuality))
                    .filter(salle -> {
                        if (maxPriceText.isEmpty()) {
                            return true;
                        }
                        try {
                            double maxPrice = Double.parseDouble(maxPriceText);
                            return salle.getPrix() <= maxPrice;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList()));

            pagination.setPageCount(calculatePageCount());
            pagination.setCurrentPageIndex(0);
            updateListViewForCurrentPage();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du filtrage des salles : " + e.getMessage());
        }
    }

    private void initializeFilters() {
        capacityFilter.getItems().addAll("Tous", "50", "100", "150", "200", "250");
        qualityFilter.getItems().addAll("Trés Bien", "Fabuleuse", "Exceptionnel", "Superbe", "Standard");
        capacityFilter.setValue("Capacité");
        qualityFilter.setValue("Qualité");
    }

    @FXML
    private void handleAccueilButton() {
        System.out.println("Accueil button clicked");
    }

    @FXML
    private void handleEvenementsButton() {
        System.out.println("Evenements button clicked");
        loadSallesFromDatabase();
    }

    @FXML
    private void handleSallesButton() {
        System.out.println("Salles button clicked");
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
        filterSalles();
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

            // Définir le callback pour recharger les salles après l'ajout
            controller.setOnSalleAddedCallback(this::loadSallesFromDatabase);

            controller.setStage(ajoutStage);
            ajoutStage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void loadSallesFromDatabase() {
        try {
            // Récupérer l'utilisateur connecté
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                return;
            }

            // Récupérer les salles de l'utilisateur connecté
            List<Salle> sallesFromDB = salleService.getSallesByUserId(currentUser.getIdUser());
            ObservableList<Salle> salles = FXCollections.observableArrayList(sallesFromDB);
            listViewSalles.setItems(salles);
            allSalles.setAll(sallesFromDB);
            filteredSalles.setAll(allSalles);
            pagination.setPageCount(calculatePageCount());
            updateListViewForCurrentPage();

            // Configuration de la ListView
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
                private final Button statButton = new Button("Voir Statistique");

                {
                    mainContent.setPrefWidth(500);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setStyle("-fx-background-color: linear-gradient(to bottom, #B19BB2, #FFFFFF); -fx-padding: 30; -fx-border-color: #B19BB2;");
                    imageView.setFitWidth(200);
                    imageView.setFitHeight(200);
                    imageView.setPreserveRatio(true);

                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 22px;");
                    detailsLabel.setStyle("-fx-font-size: 14px;");
                    priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #CBA979;");
                    modifierButton.setStyle("-fx-background-color: #8D598F; -fx-text-fill: white;");
                    annulerButton.setStyle("-fx-background-color: #8D598F; -fx-text-fill: white;");
                    statButton.setStyle("-fx-background-color: #98A695; -fx-text-fill: white;");

                    infoBox.getChildren().addAll(nameLabel, detailsLabel, priceLabel);
                    buttonBox.getChildren().addAll(modifierButton, annulerButton, statButton);
                    buttonBox.setAlignment(Pos.CENTER_RIGHT);

                    hbox.getChildren().addAll(imageView, infoBox, buttonBox);

                    modifierButton.setOnAction(event -> {
                        Salle selectedSalle = getItem();
                        if (selectedSalle != null) {
                            handleModifierButton(selectedSalle);
                        }
                    });

                    annulerButton.setOnAction(event -> {
                        Salle selectedSalle = getItem();
                        if (selectedSalle != null) {
                            handleSupprimerButton(selectedSalle);
                        }
                    });
                    statButton.setOnAction(event -> {
                        Salle selectedSalle = getItem();
                        if (selectedSalle != null) {
                            handleStatButtonClick(selectedSalle);
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
        Dialog<Salle> dialog = new Dialog<>();
        dialog.setTitle("Modifier une Salle");
        dialog.setHeaderText("Modifier les détails de la salle");

        TextField nomSalleField = new TextField(selectedSalle.getNomSalle());
        TextField capaciteField = new TextField(String.valueOf(selectedSalle.getCapacite()));
        TextField equipementField = new TextField(selectedSalle.getEquipement());
        TextField imageSalleField = new TextField(selectedSalle.getImageSalle());
        TextField locationSalleField = new TextField(selectedSalle.getLocationSalle());

        ComboBox<String> qualiteComboBox = new ComboBox<>();
        qualiteComboBox.getItems().addAll("Fabuleux", "Très bien", "Exceptionnel", "Superbe");
        qualiteComboBox.setValue(selectedSalle.getQualite());

        TextField prixField = new TextField(String.valueOf(selectedSalle.getPrix()));

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Nom de la salle:"), nomSalleField,
                new Label("Capacité:"), capaciteField,
                new Label("Équipement:"), equipementField,
                new Label("Image de la salle:"), imageSalleField,
                new Label("Localisation:"), locationSalleField,
                new Label("Qualité:"), qualiteComboBox,
                new Label("Prix:"), prixField
        ));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                selectedSalle.setNomSalle(nomSalleField.getText());
                selectedSalle.setCapacite(Integer.parseInt(capaciteField.getText()));
                selectedSalle.setEquipement(equipementField.getText());
                selectedSalle.setImageSalle(imageSalleField.getText());
                selectedSalle.setLocationSalle(locationSalleField.getText());
                selectedSalle.setQualite(qualiteComboBox.getValue());
                selectedSalle.setPrix(Double.parseDouble(prixField.getText()));
                return selectedSalle;
            }
            return null;
        });

        Optional<Salle> result = dialog.showAndWait();
        result.ifPresent(salle -> {
            try {
                salleService.modifier(salle);
                loadSallesFromDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de la modification de la salle : " + e.getMessage());
            }
        });
    }

    private void handleSupprimerButton(Salle selectedSalle) {
        // Afficher une boîte de dialogue de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette salle ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        // Attendre la réponse de l'utilisateur
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        // Si l'utilisateur confirme la suppression
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer la salle de la base de données
                salleService.supprimer(selectedSalle.getIdSalle());

                // Recharger les salles depuis la base de données
                loadSallesFromDatabase();

                // Afficher un message de succès
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Suppression réussie");
                successAlert.setHeaderText(null);
                successAlert.setContentText("La salle a été supprimée avec succès.");
                successAlert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de la suppression de la salle : " + e.getMessage());

                // Afficher un message d'erreur
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur de suppression");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Une erreur s'est produite lors de la suppression de la salle.");
                errorAlert.showAndWait();
            }
        } else {
            // L'utilisateur a annulé la suppression
            System.out.println("Suppression annulée.");
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

    @FXML
    private void handleStatButtonClick(Salle selectedSalle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/statistiquesreservation.fxml"));
            Parent root = loader.load();

            StatistiquesalleController controller = loader.getController();
            controller.setSalle(selectedSalle);

            Stage stage = new Stage();
            stage.setTitle("Statistiques de Réservation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur
        }
    }
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la page des réservations");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page. Veuillez réessayer.");
            alert.showAndWait();
        }
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
            showAlert("Impossible de revenir à la page précédente : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}