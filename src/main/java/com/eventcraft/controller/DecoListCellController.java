package com.eventcraft.controller;
import com.eventcraft.model.Decoration;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DecoListCellController extends ListCell<Decoration> {
    private final HBox content;

    private final Label nomLabel;

    private final Button detailsButton; // Nouveau bouton

    private AfficherDecorationController afficherController;

    public DecoListCellController() {
        super();

        // 🔹 Initialisation des composants

        detailsButton = new Button("Voir Détails");
        detailsButton.setStyle("-fx-background-color: #8B7355; -fx-text-fill: white; -fx-font-size: 16px;-fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-font-size: 14px; -fx-cursor: hand; -fx-pref-width: 300; -fx-font-family: \"Bahnschrift\";");



        nomLabel = new Label();
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #8D598F; ");



        detailsButton.setOnAction(event -> {
            if (getItem() != null) {
                openDecoDetail(getItem());
            }
        });



        // 🔹 Organisation des composants
        VBox textContainer = new VBox(nomLabel, detailsButton);
        textContainer.setSpacing(10);
        textContainer.setStyle("-fx-background: #FFFDF6; -fx-background-color: #FFFDF6; ");



        content = new HBox( textContainer);
        content.setSpacing(10);
        content.setStyle("-fx-background: #FFFDF6; -fx-background-color: #FFFDF6;");
    }

    private void openDecoDetail(Decoration selectedDeco) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DetailsDeco.fxml"));
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
            // 🔹 Mise à jour des labels
            nomLabel.setText("Nom : " + deco.getNom_decor());


            setGraphic(content);
        }


    }

    public void setAfficherController(AfficherDecorationController afficherController) {
        this.afficherController = afficherController;
    }

}
