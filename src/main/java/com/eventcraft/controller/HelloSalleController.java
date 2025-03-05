package com.eventcraft.controller;

import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;
import com.eventcraft.service.ReservationSalleService;
import com.eventcraft.service.SalleService;
import com.eventcraft.util.NavigationHistory;
import com.eventcraft.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.eventcraft.model.Salle;
import javafx.geometry.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HelloSalleController {
    private static final int ITEMS_PER_PAGE = 5;
    private ReservationSalleService reservationService = new ReservationSalleService();
    private final SalleService salleService = new SalleService();
    private ObservableList<Salle> allSalles = FXCollections.observableArrayList();
    private ObservableList<Salle> filteredSalles = FXCollections.observableArrayList();

    @FXML private ListView<Salle> listViewSalles;
    @FXML private HBox menuBar;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> capacityFilter;
    @FXML private ComboBox<String> qualityFilter;
    @FXML private TextField advancedPriceField;
    @FXML private VBox mainContent;
    @FXML private ImageView logoImageView;
    @FXML private Pagination pagination;
    private User user;
    private User currentUser;

    public void setUser(User user) {
        this.user = user;
        // Additional logic to handle the user object
    }
    @FXML
    private void handleSallesNavigation(ActionEvent event) {
        navigateToPage("profilsalle.fxml", event, "Gestion des salles");
    }
    @FXML
    public void initialize() {
        loadLogoImage();
        initializeFilters();
        loadSallesFromDatabase();
        setupPagination();
        Font fontAwesome = Font.loadFont(getClass().getResourceAsStream("/fonts/fontawesome.ttf"), 16);

        // Appliquer la police au Label

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

    private void loadSallesFromDatabase() {
        try {
            List<Salle> sallesFromDB = salleService.afficher();
            allSalles.setAll(sallesFromDB);
            filteredSalles.setAll(allSalles);
            pagination.setPageCount(calculatePageCount());
            updateListViewForCurrentPage();

            listViewSalles.setCellFactory(param -> new ListCell<Salle>() {
                private final HBox hbox = new HBox(10);
                private final ImageView imageView = new ImageView();
                private final VBox infoBox = new VBox(5);
                private final Label nameLabel = new Label();
                private final Label detailsLabel = new Label();
                private final Label priceLabel = new Label();
                private final Label userLabel = new Label();
                private final HBox buttonBox = new HBox(10);
                private final Button reserverButton = new Button("Réserver");
                private final Button detButton = new Button("Voir Détail");

                {
                    mainContent.setPrefWidth(500);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setStyle("-fx-background-color: linear-gradient(to bottom, #B19BB2, #FFFFFF); -fx-padding: 30; -fx-border-color: #B19BB2;");
                    imageView.setFitWidth(300);
                    imageView.setFitHeight(500);
                    imageView.setPreserveRatio(true);
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 22px;");
                    detailsLabel.setStyle("-fx-font-size: 14px;");
                    priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #CBA979;");
                    userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8D598F;");
                    reserverButton.setStyle("-fx-font-size: 16px;-fx-background-color: transparent; -fx-border-color: white; -fx-border-width:1px; -fx-text-fill: white;");
                    detButton.setStyle("-fx-font-size: 16px;-fx-background-color: transparent; -fx-border-color: white; -fx-border-width:1px; -fx-text-fill: white;");
                    infoBox.setPrefWidth(300);
                    infoBox.getChildren().addAll(nameLabel, detailsLabel, priceLabel, userLabel);
                    buttonBox.getChildren().addAll(reserverButton, detButton);
                    buttonBox.setAlignment(Pos.CENTER_RIGHT);
                    hbox.getChildren().addAll(imageView, infoBox, buttonBox);

                    reserverButton.setOnAction(event -> {
                        Salle selectedSalle = getItem();
                        if (selectedSalle != null) {
                            handleReserverButton(selectedSalle);
                        }
                    });

                    detButton.setOnAction(e -> {
                        Salle selectedSalle = getItem();
                        if (selectedSalle != null) {
                            openDetailsWindow(selectedSalle, e); // Passer l'événement (e) à openDetailsWindow
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
                        User user = UserDAO.getUserById(salle.getUserId());
                        userLabel.setText(user != null ? "👤 " + user.getNom() + " " + user.getPrenom() : "👤 Utilisateur inconnu");

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
        try {
            // Charger le fichier FXML du formulaire d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signup.fxml"));
            Parent signUpView = loader.load();

            // Créer une nouvelle scène avec le formulaire d'inscription
            Scene signUpScene = new Scene(signUpView);

            // Appliquer les styles CSS si nécessaire
            String css = getClass().getResource("/styles/styles.css").toExternalForm();
            signUpScene.getStylesheets().add(css);

            // Obtenir la fenêtre actuelle (stage) et changer la scène
            Stage stage = (Stage) menuBar.getScene().getWindow();
            stage.setScene(signUpScene);
            stage.setTitle("Inscription");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du formulaire d'inscription : " + e.getMessage());
        }
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
            VBox.setMargin(logoImageView, new Insets(30, 0, 30, 20));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReserverButton(Salle selectedSalle) {
        // Récupérer l'utilisateur connecté
        User currentUser = SessionManager.getCurrentUser();

        // Vérifier si l'utilisateur est connecté
        if (currentUser == null) {
            // Afficher une alerte pour informer l'utilisateur qu'il doit se connecter
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Non connecté");
            alert.setHeaderText("Vous devez être connecté pour effectuer une réservation.");
            alert.setContentText("Veuillez vous connecter ou vous inscrire.");
            alert.showAndWait();

            // Optionnel : Rediriger l'utilisateur vers la page de connexion
            redirectToLoginPage();
            return;
        }

        // Si l'utilisateur est connecté, ouvrir le formulaire de réservation
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservationForm.fxml"));
            Parent root = loader.load();
            com.eventcraft.controller.ReservationFormController controller = loader.getController();
            controller.setSalleId(selectedSalle.getIdSalle());
            controller.setUserId(currentUser.getIdUser()); // Utiliser l'ID de l'utilisateur connecté
            controller.setStage(new Stage());
            Stage reservationStage = new Stage();
            reservationStage.setTitle("Formulaire de Réservation");
            reservationStage.setScene(new Scene(root));
            reservationStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du formulaire de réservation : " + e.getMessage());
        }
    }
    private void redirectToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Connexion");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            // Fermer la fenêtre actuelle (optionnel)
            Stage currentStage = (Stage) menuBar.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page de connexion : " + e.getMessage());
        }
    }

    @FXML
    private void reserverButton(Salle selectedSalle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reservation.fxml"));
            Parent root = loader.load();
            ReservationSalleController reservationController = loader.getController();
            Stage reservationStage = new Stage();
            reservationStage.setTitle("Réservations");
            reservationStage.setScene(new Scene(root));
            reservationStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page des réservations : " + e.getMessage());
        }
    }
    private void openDetailsWindow(Salle selectedSalle, ActionEvent event) {
        try {
            // Load the FXML file for the details page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/detailsalle.fxml"));
            Parent root = loader.load();

            // Get the controller of the details page
            DetailsSalleController detailsController = loader.getController();

            // Initialize the room details in the controller
            detailsController.initializeDetails(selectedSalle.getIdSalle());

            // Create a new scene
            Scene scene = new Scene(root);

            // Get the screen dimensions
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            // Create a new stage
            Stage detailsStage = new Stage();
            detailsStage.setTitle("Détails de la Salle");

            // Set the size of the window to match the screen dimensions
            detailsStage.setX(bounds.getMinX());
            detailsStage.setY(bounds.getMinY());
            detailsStage.setWidth(bounds.getWidth());
            detailsStage.setHeight(bounds.getHeight());

            // Close the current window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Set the scene and show the new window
            detailsStage.setScene(scene);
            detailsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la fenêtre des détails : " + e.getMessage());
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
    @FXML
    private void handleMainNavigation(ActionEvent event) {
        try {
            // Add current page to navigation history before navigating
            NavigationHistory.addPage("/view/salle.fxml");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Parent root = loader.load();

            // Assuming there's a MainController to initialize
            MainDashboardController mainDashboardController = loader.getController();
            if (currentUser != null) {
                mainDashboardController.setUser(currentUser);
            } else {
            }

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load main view: " + e.getMessage(), Alert.AlertType.ERROR);
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
