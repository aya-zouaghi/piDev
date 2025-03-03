package Controllors;

import entities.Decoration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class DecoListCellController extends ListCell<Decoration> {
    private final HBox content;
    private final Label nomLabel;
    private final Button detailsButton;
    private final ImageView imageView;
    private AfficherDecorationController afficherController;

    public DecoListCellController() {
        super();
        // 🔹 Initialisation des composants
        imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-radius: 10px; -fx-border-color: #ccc;");
        imageView.getStyleClass().add("image-container");
        // Bouton "Voir Détails"
        detailsButton = new Button("Voir Détails");
        detailsButton.setStyle("-fx-background-color: #8B7355; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 8px 16px; -fx-border-radius: 5px; -fx-cursor: hand;");

        // Label pour le nom de la décoration
        nomLabel = new Label();
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #8D598F;");

        // Espacement flexible pour pousser le bouton à droite
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action du bouton "Voir Détails"
        detailsButton.setOnAction(event -> {
            if (getItem() != null) {
                openDecoDetail(getItem());
            }
        });

        // Organisation en HBox (nom à gauche, bouton à droite)
        content = new HBox(imageView,nomLabel, spacer, detailsButton);
        content.setSpacing(10);
        content.setStyle("-fx-padding: 15px; -fx-background-color: #FFFDF6; -fx-border-color: #ccc; -fx-border-radius: 10px;");
    }

    private void openDecoDetail(Decoration selectedDeco) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsDeco.fxml"));
            Parent root = loader.load();

            DetailsDecoController controller = loader.getController();
            controller.setDeco(selectedDeco);
            controller.setSelectedDeco(selectedDeco);
            if (afficherController != null) {
                controller.setAfficherController(afficherController);
            } else {
                System.out.println("❌ ERREUR : afficherController est NULL dans openDecoDetail() !");
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de décoration");
            stage.setResizable(false);
            stage.show();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Decoration deco, boolean empty) {
        super.updateItem(deco, empty);
        if (empty || deco == null) {
            setGraphic(null);
        } else {
            nomLabel.setText("Nom : " + deco.getNom_decor());
            // 🛒 Affichage d'une étiquette pour le stock
            String stockLabel;
            if (deco.getStock() == 0) {
                stockLabel = " (⚠️ Rupture de stock)";
                nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");
            } else if (deco.getStock() <= 5) { // Seuil pour stock faible
                stockLabel = " (⚠️ Stock faible)";
                nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: orange;");
            } else {
                stockLabel = "";
                nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #8D598F;");
            }
            nomLabel.setText("Nom : " + deco.getNom_decor() + stockLabel);

            if (deco.getImageDeco() != null && !deco.getImageDeco().isEmpty()) {
                File imageFile = new File("src/main/resources/images/" + deco.getImageDeco());
                if (imageFile.exists()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.out.println("❌ Image non trouvée pour : " + deco.getImageDeco());
                    imageView.setImage(null); // ou une image par défaut
                }
            } else {
                imageView.setImage(null); // si aucune image n'est définie
            }

            setGraphic(content);
        }

    }

    public void setAfficherController(AfficherDecorationController afficherController) {
        this.afficherController = afficherController;
    }
}
