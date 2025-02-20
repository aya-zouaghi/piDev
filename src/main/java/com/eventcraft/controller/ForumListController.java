package com.eventcraft.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.eventcraft.model.Forum;
import com.eventcraft.service.ForumService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ForumListController {

    @FXML
    private ListView<Forum> forumListView;

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    private ForumService forumService;
    private ObservableList<Forum> forumList = FXCollections.observableArrayList();

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
    }

    public void loadForums() {
        try {
            List<Forum> forums = forumService.afficher();
            forumList.setAll(forums);
            forumListView.setItems(forumList);
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

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    VBox textContainer = new VBox(titleLabel, descriptionLabel, dateCreation);
                    HBox cellContainer = new HBox(textContainer, spacer, editButton, deleteButton);
                    cellContainer.setSpacing(10);

                    setGraphic(cellContainer);
                }
            }
        });
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/commentaire-view.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UpdateForum.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Welcome.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Accueil");
        } catch (IOException e) {
            System.err.println("Erreur: Impossible de retourner à l'accueil: " + e.getMessage());
        }
    }

    @FXML
    private void onAddButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ajout-form.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ajouter un Forum");

        } catch (IOException e) {
            System.err.println("Erreur: Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage());
        }
    }
}
