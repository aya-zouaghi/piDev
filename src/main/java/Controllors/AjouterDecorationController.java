package Controllors;

import entities.Decoration;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.ServiceDecoration;
import javafx.stage.FileChooser;
import java.io.IOException;
import java.sql.SQLException;
import java.nio.file.Files;
import java.io.File;

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
    private Button btnChoisirImage;
    @javafx.fxml.FXML
    private ImageView imageview;
    private File imageFile;
    @javafx.fxml.FXML
    public void AjouterDecoration(ActionEvent actionEvent) {



        // Récupérer les valeurs des champs
        String titre = tf_titre.getText().trim();
        String typeDeco = tf_typeDeco.getText().trim();
        String description = tf_description.getText().trim();
        String prixText = tf_prix.getText().trim();
        String stockText = tf_stock.getText().trim();

        // Validation des champs
        if (titre.isEmpty() || typeDeco.isEmpty() || description.isEmpty() || prixText.isEmpty() || stockText.isEmpty()) {
            afficherAlerte(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

        // Validation du prix (doit être un nombre positif)
        float prix;
        try {
            prix = Float.parseFloat(prixText);
            if (prix <= 0) {
                afficherAlerte(Alert.AlertType.ERROR, "Prix invalide", "Le prix doit être un nombre positif !");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Prix invalide", "Veuillez entrer un prix valide !");
            return;
        }

        // Validation du stock (doit être un entier positif)
        int stock;
        try {
            stock = Integer.parseInt(stockText);
            if (stock < 0) {
                afficherAlerte(Alert.AlertType.ERROR, "Stock invalide", "Le stock doit être un entier positif !");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Stock invalide", "Veuillez entrer un stock valide !");
            return;
        }

        // Validation de l'image
        if (imageFile == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Image manquante", "Veuillez choisir une image pour la décoration !");
            return;
        }

        // Si toutes les validations passent, on ajoute la décoration
        ServiceDecoration serviceDecoration = new ServiceDecoration();
        Decoration Deco = new Decoration(titre, typeDeco, description, prix, stock, imageFile.getName());

        try {
            // Sauvegarde de l'image
            File destDir = new File("src/main/resources/images/");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File destFile = new File(destDir, imageFile.getName());
            Files.copy(imageFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image copiée avec succès dans " + destFile.getAbsolutePath());


            // Ajouter dans la base
            serviceDecoration.ajouter(Deco);
            System.out.println("Décoration ajoutée en base : " + Deco);

            // Message de succès
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Décoration ajoutée avec succès !");

            // Redirection vers la liste des décorations
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherDecoration.fxml"));
            tf_titre.getScene().setRoot(root);
            System.out.println("Redirection réussie !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.err.println("Erreur SQL : " + e.getMessage());

            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter la décoration !");
        } catch (IOException e) {
            System.err.println("Erreur de chargement FXML : " + e.getMessage());

            afficherAlerte(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger l'affichage !");
            e.printStackTrace();
        }
    }
    @javafx.fxml.FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Sauvegarde uniquement le nom du fichier au lieu du chemin absolu
            imageFile = selectedFile;
            imageview.setImage(new Image(imageFile.toURI().toString()));
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
