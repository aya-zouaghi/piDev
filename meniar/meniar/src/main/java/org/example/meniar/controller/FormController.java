package org.example.meniar.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.meniar.entities.Forum;
import org.example.meniar.services.ForumService;
import org.example.meniar.utils.BadWordFilter;
import org.example.meniar.utils.TranslationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class FormController {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField userIdField;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private Label idForumLabel;

    private ForumService forumService;
    private Forum forumToUpdate;

    @FXML
    private void initialize() {
        forumService = new ForumService();
    }

    @FXML
    private void onAddForumClick() {
        try {
            String title = titreField.getText();
            String description = descriptionField.getText();

            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Le titre et la description sont obligatoires.");
                return;
            }

            if (datePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "La date est obligatoire.");
                return;
            }

            // Check for bad words
            if (BadWordFilter.containsBadWords(title) || BadWordFilter.containsBadWords(description)) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Le titre ou la description contient des mots inappropriés.");
                return;
            }

            Forum forum = new Forum(
                    0,
                    title,
                    description,
                    java.sql.Date.valueOf(datePicker.getValue()),
                    Integer.parseInt(userIdField.getText())
            );

            try {
                forumService.ajouter(forum);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum ajouté avec succès !");
                redirectToForumList();
            } catch (SQLException e) {
                if (e.getMessage().contains("foreign key constraint fails")) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID de l'utilisateur spécifié n'existe pas dans la base de données.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le forum : " + e.getMessage());
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    private void redirectToForumList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum-list.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Forums");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des forums.");
        }
    }

    @FXML
    private void onUpdateForumClick() {
        if (forumToUpdate == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun forum sélectionné pour la mise à jour !");
            return;
        }

        try {
            forumToUpdate.setTitreForum(titreField.getText());
            forumToUpdate.setDescriptionForum(descriptionField.getText());
            forumToUpdate.setDateCreation(java.sql.Date.valueOf(datePicker.getValue()));
            forumToUpdate.setUserId(Integer.parseInt(userIdField.getText()));

            forumService.modifier(forumToUpdate);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum modifié avec succès !");
            clearFields();

            ForumListController.refreshList();
            redirectToForumList();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le forum : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        titreField.clear();
        descriptionField.clear();
        datePicker.setValue(null);
        userIdField.clear();
        idForumLabel.setText("");
    }

    public void initData(Forum forum) {
        if (forum == null) {
            System.err.println("Erreur: forum reçu est null !");
            return;
        }

        this.forumToUpdate = forum;
        System.out.println("Forum chargé: " + forum.getTitreForum());

        idForumLabel.setText("ID: " + forum.getIdForum());
        titreField.setText(forum.getTitreForum());
        descriptionField.setText(forum.getDescriptionForum());

        if (forum.getDateCreation() != null) {
            java.util.Date utilDate = forum.getDateCreation();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            datePicker.setValue(sqlDate.toLocalDate());
        }

        userIdField.setText(String.valueOf(forum.getUserId()));
    }

    @FXML
    private void onReturnClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum-list.fxml")); // Your Hello page or forum list page
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) titreField.getScene().getWindow(); // Get current window
            stage.setScene(scene);
            stage.setTitle("Liste des Forums"); // Update window title if necessary
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir à la liste des forums.");
        }
    }
}
