package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.eventcraft.model.Forum;
import com.eventcraft.service.ForumService;
import com.eventcraft.util.SessionManager;

import java.io.IOException;
import java.sql.SQLException;

public class FormController {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker datePicker;


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
        int loggedInUserId = SessionManager.getUserId();

        if (loggedInUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur connecté !");
            return;
        }

        try {
            Forum forum = new Forum(
                    0,
                    titreField.getText(),
                    descriptionField.getText(),
                    java.sql.Date.valueOf(datePicker.getValue()),
                    loggedInUserId // Automatically assign user ID
            );
            forumService.ajouter(forum);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Forum ajouté avec succès !");
            redirectToForumList();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le forum : " + e.getMessage());
        }
    }

    private void redirectToForumList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/forum-list.fxml"));
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

        int loggedInUserId = SessionManager.getUserId(); // Get logged-in user ID

        if (loggedInUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur connecté !");
            return;
        }

        try {
            forumToUpdate.setTitreForum(titreField.getText());
            forumToUpdate.setDescriptionForum(descriptionField.getText());
            forumToUpdate.setDateCreation(java.sql.Date.valueOf(datePicker.getValue()));

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

    }

    @FXML
    private void onReturnClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/forum-list.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Forums");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir à la liste des forums.");
        }
    }



}
