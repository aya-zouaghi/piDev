package Controllors;

import entities.Decoration;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import services.ServiceDecoration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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
    private TextField searchField; // Champ de recherche
    private static final int STOCK_FAIBLE_SEUIL = 5;
    private ObservableList<Decoration> allDecorations = FXCollections.observableArrayList();

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
        setupSearch();

    }
    @FXML
    private void rechercherArticle(ActionEvent event) {
        // Récupérer le texte dans le champ de recherche
        String keyword = searchField.getText();
        filterDecorations(keyword);
    }
    private void chargerDeco() {
        try {
            List<Decoration> decos = servicedeco.afficher();
            allDecorations.setAll(decos); // Remplit la liste pour le filtrage
            lv_decoration.setItems(allDecorations);
        } catch (SQLException e) {
            System.out.println("aa");
        }
    }
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDecorations(newValue);
        });
    }

    private void filterDecorations(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            lv_decoration.setItems(allDecorations);
        } else {
            ObservableList<Decoration> filteredDecorations = allDecorations.stream()
                    .filter(deco -> deco.getNom_decor().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            lv_decoration.setItems(filteredDecorations);
        }
    }
    public void rafraichirAffichage() {
        try {
            List<Decoration> decos = servicedeco.afficher();
            ObservableList<Decoration> data = FXCollections.observableArrayList(decos);


            lv_decoration.setItems(data);
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
            System.out.println(getClass().getResource("/AjouterDecoration.fxml"));

            lv_decoration.getScene().setRoot(root);
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger la page d'ajout !");
            e.printStackTrace();
        }
    }
}
