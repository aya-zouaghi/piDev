package org.example.meniar.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.meniar.entities.Forum;
import org.example.meniar.services.ForumService;
import org.example.meniar.utils.TranslationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ForumListController {

    @FXML
    private ListView<Forum> forumListView;

    @FXML
    private Button backButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    private ForumService forumService;
    private ObservableList<Forum> forumList = FXCollections.observableArrayList();
    private FilteredList<Forum> filteredForums;

    public static ForumListController instance;

    public ForumListController() {
        instance = this;
    }

    @FXML
    private void initialize() {
        forumService = new ForumService();
        loadForums();
        setupListView();
        setupForumSelectionHandler();
        setupSearch();
    }

    private void setupSearch() {
        // Initialize FilteredList
        filteredForums = new FilteredList<>(forumList, p -> true);

        // Add listener to search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterForums(newValue);
        });

        // Set up search button
        searchButton.setOnAction(event -> {
            filterForums(searchField.getText());
        });

        // Set the filtered list as the ListView's items
        forumListView.setItems(filteredForums);
    }

    private void filterForums(String searchText) {
        filteredForums.setPredicate(forum -> {
            // If search text is empty, show all forums
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();

            // Match against title and description
            return forum.getTitreForum().toLowerCase().contains(lowerCaseFilter) ||
                   forum.getDescriptionForum().toLowerCase().contains(lowerCaseFilter);
        });
    }

    public void loadForums() {
        try {
            List<Forum> forums = forumService.afficher();
            forumList.setAll(forums);
            // If filteredForums is not initialized yet (first load), don't update it
            if (filteredForums == null) {
                forumListView.setItems(forumList);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les forums : " + e.getMessage());
        }
    }

    public static void refreshList() {
        if (instance != null) {
            instance.loadForums();
        } else {
            System.err.println("Erreur: ForumListController instance is null!");
        }
    }

    private void setupListView() {
        forumListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Forum forum, boolean empty) {
                super.updateItem(forum, empty);

                if (empty || forum == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label titleLabel = new Label(forum.getTitreForum());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label descriptionLabel = new Label(forum.getDescriptionForum());
                    descriptionLabel.setWrapText(true);
                    descriptionLabel.setMaxWidth(200);

                    Label dateCreation = new Label(forum.getDateCreation().toString());
                    dateCreation.setWrapText(true);
                    dateCreation.setMaxWidth(200);

                    Button deleteButton = new Button("❌");
                    deleteButton.setStyle("-fx-background-color: #CBA979; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> deleteForum(forum));

                    Button editButton = new Button("✏️");
                    editButton.setStyle("-fx-background-color: #CBA979; -fx-text-fill: white;");
                    editButton.setOnAction(event -> redirectToUpdateForm(forum));

                    Button translateButton = new Button("🌐");
                    translateButton.setStyle("-fx-background-color: #CBA979; -fx-text-fill: white;");
                    translateButton.setOnAction(event -> showTranslationMenu(forum, titleLabel, descriptionLabel));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    VBox textContainer = new VBox(titleLabel, descriptionLabel, dateCreation);
                    HBox cellContainer = new HBox(textContainer, spacer, translateButton, editButton, deleteButton);
                    cellContainer.setSpacing(10);

                    setGraphic(cellContainer);
                }
            }
        });
    }

    private void showTranslationMenu(Forum forum, Label titleLabel, Label descriptionLabel) {
        ContextMenu menu = new ContextMenu();
        Map<String, String> languageNames = TranslationService.getLanguageNames();
        
        // Add menu items directly for each language
        for (Map.Entry<String, String> entry : languageNames.entrySet()) {
            String langCode = entry.getKey();
            String langName = entry.getValue();
            
            MenuItem langItem = new MenuItem(langName);
            langItem.setOnAction(e -> translateItemBoth(forum, langCode));
            menu.getItems().add(langItem);
        }
        
        menu.show(titleLabel, javafx.geometry.Side.RIGHT, 0, 0);
    }

    private void translateItemBoth(Forum forum, String targetLang) {
        if (forum == null) return;
        try {
            // Show translation in progress
            Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
            progressAlert.setTitle("Information");
            progressAlert.setHeaderText(null);
            progressAlert.setContentText("Traduction en cours...");
            progressAlert.show();

            // Translate both title and description
            String translatedTitle = TranslationService.translate(forum.getTitreForum(), targetLang);
            String translatedDesc = TranslationService.translate(forum.getDescriptionForum(), targetLang);
            
            // Close progress alert
            progressAlert.close();

            // Update forum with translated text
            forum.setTitreForum(translatedTitle);
            forum.setDescriptionForum(translatedDesc);
            forumService.modifier(forum);
            
            // Show success message with translations
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                String.format("Traduction réussie !\nTitre: %s\nDescription: %s", 
                    translatedTitle, translatedDesc));
            
            loadForums();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de traduction: " + e.getMessage());
        }
    }

    private void setupForumSelectionHandler() {
        forumListView.setOnMouseClicked(event -> {
            Forum selectedForum = forumListView.getSelectionModel().getSelectedItem();
            if (selectedForum != null) {
                redirectToCommentaireView(selectedForum);
            }
        });
    }

    private void redirectToCommentaireView(Forum forum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commentaire-view.fxml"));
            Scene scene = new Scene(loader.load());

            CommentaireController controller = loader.getController();
            if (controller != null) {
                controller.initData(forum);
            } else {
                System.err.println("Erreur: Impossible d'initialiser les données du forum pour les commentaires !");
            }

            Stage stage = (Stage) forumListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Commentaires du Forum");

        } catch (IOException e) {
            System.err.println("Erreur de chargement de la vue des commentaires: " + e.getMessage());
        }
    }

    private void deleteForum(Forum forum) {
        try {
            forumService.supprimer(forum.getIdForum());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum supprimé avec succès !");
            loadForums();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le forum : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void redirectToUpdateForm(Forum forum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateForum.fxml"));
            Scene scene = new Scene(loader.load());

            FormController controller = loader.getController();
            if (controller != null) {
                controller.initData(forum);
            } else {
                System.err.println("Erreur: Impossible d'initialiser les données du formulaire !");
            }

            Stage stage = (Stage) forumListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Modifier un Forum");

        } catch (IOException e) {
            System.err.println("Erreur de chargement du formulaire de mise à jour: " + e.getMessage());
        }
    }

    @FXML
    private void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hello-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Accueil");
        } catch (IOException e) {
            System.err.println("Erreur: Impossible de retourner à l'accueil: " + e.getMessage());
        }
    }
}
