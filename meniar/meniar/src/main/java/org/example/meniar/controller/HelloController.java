package org.example.meniar.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.meniar.services.ForumService;
import org.example.meniar.services.CommentaireService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class HelloController {

    @FXML
    private Button startButton;

    @FXML
    private Button forumListButton;

    @FXML
    private Text totalForumsText;

    @FXML
    private Text totalCommentsText;

    @FXML
    private Text activeUsersText;

    private ForumService forumService;
    private CommentaireService commentaireService;

    @FXML
    private void initialize() {
        forumService = new ForumService();
        commentaireService = new CommentaireService();
        updateStatistics();
    }

    private void updateStatistics() {
        try {
            // Get total forums
            int totalForums = forumService.afficher().size();
            totalForumsText.setText(String.valueOf(totalForums));

            // Get total comments
            int totalComments = commentaireService.afficher().size();
            totalCommentsText.setText(String.valueOf(totalComments));

            // Get unique active users (combining forum creators and commenters)
            Set<Integer> activeUsers = new HashSet<>();
            
            // Add forum creators
            forumService.afficher().forEach(forum -> activeUsers.add(forum.getUserId()));
            
            // Add comment authors
            commentaireService.afficher().forEach(comment -> activeUsers.add(comment.getUserId()));

            activeUsersText.setText(String.valueOf(activeUsers.size()));

        } catch (SQLException e) {
            System.err.println("Error updating statistics: " + e.getMessage());
            // Set default values in case of error
            totalForumsText.setText("--");
            totalCommentsText.setText("--");
            activeUsersText.setText("--");
        }
    }

    @FXML
    private void onStartButtonClick() {
        loadScene("/ajout-form.fxml", "Ajouter un Forum");
    }

    @FXML
    private void onForumListButtonClick() {
        loadScene("/forum-list.fxml", "Liste des Forums");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page: " + fxmlPath);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
