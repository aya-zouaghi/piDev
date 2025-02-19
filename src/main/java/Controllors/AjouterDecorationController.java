package Controllors;

import entities.Decoration;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.ServiceDecoration;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterDecorationController {
    @javafx.fxml.FXML
    private TextField tf_titre;
    @javafx.fxml.FXML
    private TextField tf_prix;
    @javafx.fxml.FXML
    private TextField tf_stock;
    @javafx.fxml.FXML
    private TextField tf_typeDeco;
    @javafx.fxml.FXML
    private TextField tf_description;

    @javafx.fxml.FXML
    public void AjouterDecoration(ActionEvent actionEvent) {
        ServiceDecoration serviceDecoration = new ServiceDecoration();
        Decoration Deco = new Decoration(
                tf_titre.getText(),
                tf_typeDeco.getText(),
                tf_description.getText(),
                Float.parseFloat(tf_prix.getText()),
                Integer.parseInt(tf_stock.getText()));
        try {
            serviceDecoration.ajouter(Deco);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Decoration ajoute");
            alert.showAndWait();
            //Redirection vers la liste des décorations
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherDecoration.fxml"));
            tf_titre.getScene().setRoot(root);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger l'affichage !");
            e.printStackTrace();
        }
    }
    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @javafx.fxml.FXML
    public void annulerDeco(ActionEvent actionEvent) {
        tf_titre.clear();
        tf_prix.clear();
        tf_stock.clear();
        tf_typeDeco.clear();
        tf_description.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherDecoration.fxml"));
            tf_titre.getScene().setRoot(root);

        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger la page d'ajout !");
            e.printStackTrace();
        }
    }

    @javafx.fxml.FXML
    public void AfficherDeco(ActionEvent actionEvent) {
        try {
            Parent root= FXMLLoader.load(getClass().getResource("/AfficherDecoration.fxml"));
            tf_titre.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
