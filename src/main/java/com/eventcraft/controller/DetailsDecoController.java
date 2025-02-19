package com.eventcraft.controller;
import com.eventcraft.model.Decoration;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import com.eventcraft.service.ServiceDecoration;
import java.io.IOException;

public class DetailsDecoController {
    @javafx.fxml.FXML
    private Label lblNom;
    @javafx.fxml.FXML
    private Label lblPrix;
    @javafx.fxml.FXML
    private Label lblType;
    @javafx.fxml.FXML
    private Label lblStock;
    @javafx.fxml.FXML
    private Label lblDescription;
    private Decoration selectedDeco;
    private Stage currentStage;
    private ServiceDecoration serviceDecoration = new ServiceDecoration();
    private AfficherDecorationController afficherController;
    @javafx.fxml.FXML
    private Button btnCommander;

    public void setDeco(Decoration selectedDeco) {
        if (selectedDeco != null) {
            lblNom.setText(selectedDeco.getNom_decor());
            lblType.setText(selectedDeco.getType_decor());
            lblDescription.setText(selectedDeco.getDescription_decor());
            lblPrix.setText(String.valueOf(selectedDeco.getPrix()));
            lblStock.setText(String.valueOf(selectedDeco.getStock()));
        }
    }
    public void setSelectedDeco(Decoration selectedDeco) {
        this.selectedDeco = selectedDeco;

    }
    @javafx.fxml.FXML
    public void supprimerDeco(ActionEvent actionEvent) {
        if (selectedDeco != null) {
            System.out.println("Suppression de la décoration avec l'ID: " + selectedDeco.getNom_decor());
            try {
                serviceDecoration.supprimer(selectedDeco.getId_decor());

                showAlert("Success", "La décoration a été supprimée avec succès.", Alert.AlertType.INFORMATION);
                // Rafraîchir la liste des décorations
                if (afficherController != null) {
                    afficherController.rafraichirAffichage();
                } else {
                    System.out.println("❌ ERREUR : afficherController est NULL !");
                }

                //fermer la fenetre de details
                Stage stage = (Stage) lblNom.getScene().getWindow();
                stage.close();

            }catch (Exception e) {
                showAlert("Error", "Une erreur est survenue lors de la suppression.", Alert.AlertType.ERROR);

                e.printStackTrace();
            }
    }else {
            showAlert("Erreur", "Aucune décoration sélectionnée à supprimer.", Alert.AlertType.WARNING);

        }
}
    public void setAfficherController(AfficherDecorationController afficherController) {
        this.afficherController = afficherController;
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @javafx.fxml.FXML
    public void modifierDetails(ActionEvent actionEvent) {
        if (selectedDeco != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifierDecoration.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur de la page ModifierDecoration.fxml
                ModifierDecorationController modifierController = loader.getController();
                modifierController.setDecoration(selectedDeco); // Passer l'objet décoration sélectionné
                modifierController.setAfficherController(afficherController);
                // Ouvrir une nouvelle fenêtre
                Stage stage = new Stage();
                stage.setTitle("Modifier Décoration");
                stage.setScene(new javafx.scene.Scene(root));
                stage.show();

                // Fermer la fenêtre actuelle (détails)
                Stage currentStage = (Stage) lblNom.getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Erreur", "Aucune décoration sélectionnée pour modification.", Alert.AlertType.WARNING);
        }
    }

    @javafx.fxml.FXML
    public void AnnulerDetails(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AfficherDecoration.fxml"));
            Parent root = loader.load();

            // Remplacer la scène actuelle avec la nouvelle page
            Stage stage = (Stage) lblNom.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des décorations.", Alert.AlertType.ERROR);
        }
    }

    @javafx.fxml.FXML
    public void CommanderArticle(ActionEvent actionEvent) {
        if (selectedDeco != null) {
            try {
                // Charger la scène de commande de la décoration
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CommanderDeco.fxml"));
                Parent root = loader.load();

                // Initialiser le contrôleur de la page de commande
                CommanderDecoController commanderController = loader.getController();
                commanderController.setDecoration(selectedDeco);  // Passer la décoration sélectionnée

                // Afficher la nouvelle scène de commande
                Stage stage = new Stage();
                stage.setTitle("Commander Décoration");
                stage.setScene(new javafx.scene.Scene(root));
                stage.show();
                Stage currentStage = (Stage) lblNom.getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la page de commande.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Aucune décoration sélectionnée pour la commande.", Alert.AlertType.WARNING);
        }
    }
}