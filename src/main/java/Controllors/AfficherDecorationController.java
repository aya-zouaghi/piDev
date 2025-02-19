package Controllors;

import entities.Decoration;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import services.ServiceDecoration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherDecorationController {

    @FXML
    private AnchorPane affiche_listDeco;
    @FXML
    private ScrollPane Scroll_listeDeco;
    @FXML
    private Pane liste_Deco;
    @FXML
    private ListView<Decoration> lv_decoration;
    private static final ServiceDecoration servicedeco = new ServiceDecoration();
    @FXML
    void initialize() {
        if (lv_decoration == null) {
            System.out.println("❌ ERREUR : listView est NULL dans initialize()");
            return;
        }
        lv_decoration.setCellFactory(param -> {
            DecoListCellController cellController = new DecoListCellController();
            cellController.setAfficherController(this);  // 🔹 Passer le contrôleur principal
            return cellController;
        });
        chargerDeco();


    }

    private void chargerDeco() {
        try {
            List<Decoration> decos = servicedeco.afficher();
            ObservableList<Decoration> data = FXCollections.observableArrayList(decos);
            lv_decoration.setItems(data);
        } catch (SQLException e) {
            System.out.println("aa");
        }
    }
    public void rafraichirAffichage() {
        try {
            List<Decoration> decos = servicedeco.afficher();
            lv_decoration.setItems(FXCollections.observableArrayList(decos));
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

    @FXML
    public void ajouterArticle(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterDecoration.fxml"));
            lv_decoration.getScene().setRoot(root);
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger la page d'ajout !");
            e.printStackTrace();
        }
    }
}
