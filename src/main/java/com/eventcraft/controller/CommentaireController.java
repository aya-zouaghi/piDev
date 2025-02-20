package com.eventcraft.controller;

import com.eventcraft.model.User;
import com.eventcraft.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.eventcraft.model.Commentaire;
import com.eventcraft.model.Forum;
import com.eventcraft.service.CommentaireService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CommentaireController {

    @FXML
    private Label forumTitleLabel, forumDescriptionLabel;

    @FXML
    private ListView<Commentaire> commentsListView;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Button addCommentButton;

    @FXML
    private Button backButton;

    private final CommentaireService commentaireService = new CommentaireService();
    private ObservableList<Commentaire> commentsList = FXCollections.observableArrayList();
    private Forum forum;
    private User currentUser;

    public void initData(Forum selectedForum) {
        this.forum = selectedForum;
        this.currentUser = SessionManager.getCurrentUser();
        forumTitleLabel.setText(forum.getTitreForum());
        forumDescriptionLabel.setText(forum.getDescriptionForum());
        loadComments();
    }

    private void loadComments() {
        try {
            List<Commentaire> commentaires = commentaireService.afficher();
            commentsList.setAll(commentaires.stream()
                    .filter(c -> c.getForumId() == forum.getIdForum())
                    .toList());
            commentsListView.setItems(commentsList);

            commentsListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Commentaire comment, boolean empty) {
                    super.updateItem(comment, empty);
                    if (empty || comment == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        Label commentLabel = new Label("Utilisateur " + comment.getUserId() + ": " + comment.getContenu());
                        vbox.getChildren().add(commentLabel);

                        if (comment.getUserId() == currentUser.getIdUser()) { // Only show buttons for the owner
                            HBox buttonBox = new HBox(10);
                            Button updateButton = new Button("Update");
                            Button deleteButton = new Button("Delete");

                            updateButton.setOnAction(event -> onUpdateCommentClick(comment));
                            deleteButton.setOnAction(event -> onDeleteCommentClick(comment));

                            buttonBox.getChildren().addAll(updateButton, deleteButton);
                            vbox.getChildren().add(buttonBox);
                        }

                        setGraphic(vbox);
                    }
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les commentaires.");
        }
    }


    @FXML
    private void onAddCommentClick() {
        String content = commentTextArea.getText().trim();
        if (content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Le commentaire ne peut pas être vide.");
            return;
        }

        Commentaire newComment = new Commentaire(0, content, new java.util.Date(), currentUser.getIdUser(), forum.getIdForum());
        try {
            commentaireService.ajouter(newComment);
            commentTextArea.clear();
            loadComments();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le commentaire.");
        }
    }

    @FXML
    private void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/forum-list.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir à la liste.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void onUpdateCommentClick(Commentaire comment) {
        TextInputDialog dialog = new TextInputDialog(comment.getContenu());
        dialog.setTitle("Update Comment");
        dialog.setHeaderText("Edit your comment");
        dialog.setContentText("New content:");

        dialog.showAndWait().ifPresent(newContent -> {
            comment.setContenu(newContent);
            comment.setDatePublication(new java.util.Date());

            try {
                commentaireService.modifier(comment);
                loadComments();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le commentaire.");
            }
        });
    }

    private void onDeleteCommentClick(Commentaire comment) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Delete Comment");
        confirmationDialog.setHeaderText("Are you sure you want to delete this comment?");
        confirmationDialog.setContentText("Comment: " + comment.getContenu());

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    commentaireService.supprimer(comment.getIdCommentaire());
                    loadComments();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le commentaire.");
                }
            }
        });
    }

}
