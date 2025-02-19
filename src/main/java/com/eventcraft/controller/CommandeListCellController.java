package com.eventcraft.controller;
import com.eventcraft.model.CommandeDecoration;
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

public class CommandeListCellController extends ListCell<CommandeDecoration> {
    private final HBox content;
    private final Label quantiteLabel;
    private final Label dateLabel;
    private final Label prixLabel;
    private final Label nomDecorationLabel;
    private final Button detailsButton;
    public CommandeListCellController() {
        super();

        detailsButton = new Button("Voir Détails");
        detailsButton.setStyle("-fx-background-color: #8B7355; -fx-text-fill: white; -fx-font-size: 16px;-fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-font-size: 14px; -fx-cursor: hand;    -fx-pref-width: 300;    -fx-font-family: \"Bahnschrift\";");

        nomDecorationLabel = new Label();
        nomDecorationLabel.setStyle("-fx-font-size: 18px;-fx-font-weight: bold; -fx-text-fill: #8D598F;");
        quantiteLabel = new Label();
        quantiteLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        prixLabel = new Label();
        prixLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333;");

        detailsButton.setOnAction(event -> {
            if (getItem() != null) {
                openCommandeDetail(getItem());
            }
        });

        VBox textContainer = new VBox(nomDecorationLabel,quantiteLabel, dateLabel, prixLabel, detailsButton);
        textContainer.setSpacing(10);
        textContainer.setStyle("-fx-background: #FFFDF6; -fx-background-color: #FFFDF6; ");


        content = new HBox(textContainer);
        content.setSpacing(10);
        content.setStyle("-fx-background: #FFFDF6; -fx-background-color: #FFFDF6;");

    }
    private void openCommandeDetail(CommandeDecoration selectedCommande) {
        try {
            System.out.println(selectedCommande.getId_commande());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DetailsCommande.fxml"));
            Parent root = loader.load();

            DetailsCommandeController controller = loader.getController();
            controller.setCommande(selectedCommande);

            Stage stage = (Stage) getScene().getWindow();

            Object userData = stage.getUserData();
            if (userData instanceof AfficherCommandeController) {
                controller.setAfficherController((AfficherCommandeController) userData);
            } else {
                System.out.println("⚠ Problème : afficherController non trouvé !");
            }


            Stage detailStage  = new Stage();
            detailStage.setScene(new Scene(root));
            detailStage.setTitle("Détails de la commande");
            detailStage.setResizable(false);
            detailStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void updateItem(CommandeDecoration commande, boolean empty) {
        super.updateItem(commande, empty);
        if (empty || commande == null) {
            setGraphic(null);
        } else {
            nomDecorationLabel.setText("Décoration : " + commande.getDecoration().getNom_decor());
            quantiteLabel.setText("Quantité : " + commande.getQuantité());
            dateLabel.setText("Date : " + commande.getDate_commande().toString());
            prixLabel.setText("Prix : " + commande.getPrix() + " dt");
            setGraphic(content);
        }
    }

}
