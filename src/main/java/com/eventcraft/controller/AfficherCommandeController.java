package com.eventcraft.controller;
import com.eventcraft.model.CommandeDecoration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.eventcraft.service.ServiceCommandeDeco;


import java.sql.SQLException;
import java.util.List;

public class AfficherCommandeController {
    @javafx.fxml.FXML
    private AnchorPane affiche_listCommande;
    @javafx.fxml.FXML
    private ScrollPane Scroll_listeCommande;
    @javafx.fxml.FXML
    private Pane liste_Commande;
    @javafx.fxml.FXML
    private ListView lv_commande;

    private static final ServiceCommandeDeco serviceCommande = new ServiceCommandeDeco();
    @FXML
    void initialize() {
        if (lv_commande == null) {
            System.out.println("❌ ERREUR : listView est NULL dans initialize()");
            return;
        }

        lv_commande.setCellFactory(param -> new CommandeListCellController());

        lv_commande.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.setUserData(this);
            }
        });


        chargerCommandes();
    }

    private void chargerCommandes() {
        try {
            List<CommandeDecoration> commandes = serviceCommande.afficher();
            ObservableList<CommandeDecoration> data = FXCollections.observableArrayList(commandes);
            lv_commande.setItems(data);
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les commandes : " + e.getMessage());
        }
    }
    public void rafraichirAffichageCommande() {
        try {
            List<CommandeDecoration> commandes = serviceCommande.afficher();
            ObservableList<CommandeDecoration> nouvelleListe = FXCollections.observableArrayList(commandes);

            // 🚀 Mise à jour correcte de la liste
            lv_commande.getItems().setAll(nouvelleListe);
            lv_commande.refresh(); // Force l'actualisation//
              } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de rafraîchir l'affichage : " + e.getMessage());
        }
    }

    private static void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
