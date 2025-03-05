package org.example.eventcraft.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.eventcraft.SERVICE.Crudoffre;
import org.example.eventcraft.entites.Offre;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OffreController {

    @FXML
    private ListView<Offre> offreListView; // Now holds Offre objects
    @FXML
    private Button addButton;

    private Crudoffre crudOffre = new Crudoffre();

    @FXML
    private void initialize() {
        offreListView.setCellFactory(param -> new OffreListCell()); // Custom cell factory
        loadOffres();

        // Add click listener for the ListView
        offreListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double click to open details
                Offre selectedOffre = offreListView.getSelectionModel().getSelectedItem();
                if (selectedOffre != null) {
                    showOffreDetails(selectedOffre);
                }
            }
        });
    }

    @FXML
    private Button demandeButton; // Button to navigate to Demandeoffre.fxml

    @FXML
    private void handleDemandeOffre(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/Demandeoffre.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de demande", e.getMessage());
        }
    }

    private void loadOffres() {
        try {
            List<Offre> offres = crudOffre.afficher();
            ObservableList<Offre> offreData = FXCollections.observableArrayList(offres);
            offreListView.setItems(offreData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les offres", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddOffre(ActionEvent event) {
        try {
            // Load the "Ajouter Offre" FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/AjoutOffre.fxml"));
            Parent root = loader.load();

            // Create a new Stage (window) for the Ajouter Offre page
            Stage stage = new Stage();
            stage.setTitle("Ajouter Offre"); // Set the title of the stage
            stage.setScene(new Scene(root));

            // Refresh the list after the "Ajouter Offre" window is closed
            stage.setOnHidden(e -> loadOffres()); // Reload the offers list

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page d'ajout", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showOffreDetails(Offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/Offredetails .fxml"));
            Parent root = loader.load();

            OffreDetails detailsController = loader.getController();
            detailsController.setOffre(offre);
            detailsController.setOffreController(this); // Pass OffreController instance

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir les détails de l'offre", e.getMessage());
        }
    }

    public void refreshOffres() {
        try {
            List<Offre> offres = crudOffre.afficher(); // Fetch the updated list of offers from the database
            ObservableList<Offre> offreData = FXCollections.observableArrayList(offres); // Create an ObservableList from the fetched data
            offreListView.setItems(offreData); // Update the ListView with the new data
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de rafraîchir les offres", e.getMessage());
            e.printStackTrace(); // Print the error stack trace for debugging
        }
    }

    // Inner class for custom ListView cell
    static class OffreListCell extends ListCell<Offre> {
        @Override
        protected void updateItem(Offre offre, boolean empty) {
            super.updateItem(offre, empty);
            if (empty || offre == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox offerBox = new VBox(10);
                offerBox.setPadding(new Insets(15));
                offerBox.setStyle("-fx-background-color: #fbf5da; -fx-border-color: #DDD; -fx-border-radius: 5px;");
                offerBox.setMaxWidth(Double.MAX_VALUE);

                Label titreLabel = new Label("Titre: " + offre.getTitreOffre());
                Label montantLabel = new Label("Montant: " + offre.getMontant());
                titreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                montantLabel.setStyle("-fx-font-size: 16px;");

                // Create a star-based rating system
                HBox starContainer = new HBox(5);
                List<ImageView> stars = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    ImageView star = new ImageView(new Image(getClass().getResourceAsStream("/org/example/eventcraft/images/good.png")));
                    star.setFitWidth(25);
                    star.setFitHeight(25);
                    int ratingValue = i;
                    star.setOnMouseClicked(event -> {
                        // Set the rating and update the UI and backend
                        setRating(offre, ratingValue, stars);
                    });
                    stars.add(star);
                    starContainer.getChildren().add(star);
                }
                // Set the initial rating visually
                setRating(offre, (int) offre.getRating(), stars);

                offerBox.getChildren().addAll(titreLabel, montantLabel, new Text("Rate this offer:"), starContainer);

                setGraphic(offerBox);
                setText(null);
            }
        }

        // Update the rating visual representation
        private void setRating(Offre offre, int ratingValue, List<ImageView> stars) {
            // Update the UI by changing the image of each star
            for (int i = 0; i < 5; i++) {
                ImageView star = stars.get(i);
                if (i < ratingValue) {
                    // Highlight the star to indicate it's selected
                    star.setImage(new Image(getClass().getResourceAsStream("/org/example/eventcraft/images/images.png")));
                } else {
                    // Set to unfilled star for the non-selected stars
                    star.setImage(new Image(getClass().getResourceAsStream("/org/example/eventcraft/images/fergha.png")));
                }
            }

            // Save the rating value to the offer (this part depends on your model)
            offre.setRating(ratingValue);

            // Assuming you have a method to save the rating to the backend (Crudoffre class)
            Crudoffre crudOffre = new Crudoffre();
            try {
                crudOffre.updateOffreRating(offre);  // Call the method to save the rating to the database
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception (show an error alert, for example)
            }
        }
    }
}