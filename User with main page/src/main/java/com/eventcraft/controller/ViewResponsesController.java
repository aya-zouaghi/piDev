package com.eventcraft.controller;

import com.eventcraft.model.Reclamation;
import com.eventcraft.model.ReclamationWithResponse;
import com.eventcraft.model.Reponse;
import com.eventcraft.model.User;
import com.eventcraft.service.ReclamationService;
import com.eventcraft.service.ReponseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewResponsesController implements Initializable {

    @FXML
    private ListView<ReclamationWithResponse> responsesListView;

    private User user;
    private final ReclamationService reclamationService;
    private final ReponseService reponseService;
    private ObservableList<ReclamationWithResponse> reclamationWithResponseList;

    public ViewResponsesController() {
        this.reclamationService = new ReclamationService();
        this.reponseService = new ReponseService();
        this.reclamationWithResponseList = FXCollections.observableArrayList();
    }

    public void setUser(User user) {
        this.user = user;
        loadReclamationWithResponseData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        responsesListView.setItems(reclamationWithResponseList);
        responsesListView.setCellFactory(this::createReclamationWithResponseListCell);
    }

    private void loadReclamationWithResponseData() {
        List<Reclamation> reclamations = reclamationService.getReclamationsByUser(user);
        List<ReclamationWithResponse> reclamationWithResponses = new ArrayList<>();

        for (Reclamation reclamation : reclamations) {
            Reponse reponse = reponseService.getReponseByReclamationId(reclamation.getId());
            reclamationWithResponses.add(new ReclamationWithResponse(reclamation, reponse));
        }

        reclamationWithResponseList.setAll(reclamationWithResponses);
    }

    private ListCell<ReclamationWithResponse> createReclamationWithResponseListCell(ListView<ReclamationWithResponse> listView) {
        return new ListCell<ReclamationWithResponse>() {
            @Override
            protected void updateItem(ReclamationWithResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Reclamation reclamation = item.getReclamation();
                    Reponse reponse = item.getReponse();

                    VBox card = new VBox();
                    card.getStyleClass().add("reclamation-cell");
                    card.setSpacing(10);
                    card.setPadding(new Insets(10));

                    Label titleLabel = new Label("📌 " + reclamation.getTitre());
                    titleLabel.getStyleClass().add("reclamation-title");

                    Label descriptionLabel = new Label("📝 " + reclamation.getDescription());
                    descriptionLabel.getStyleClass().add("reclamation-description");
                    descriptionLabel.setWrapText(true);

                    Label statusLabel = new Label();
                    if (reponse != null) {
                        statusLabel.setText("✅ Status: Resolved");
                        statusLabel.getStyleClass().add("status-resolved");
                    } else {
                        statusLabel.setText("⏳ Status: Pending");
                        statusLabel.getStyleClass().add("status-pending");
                    }

                    card.getChildren().addAll(titleLabel, descriptionLabel, statusLabel);

                    Separator separator = new Separator();
                    separator.setPadding(new Insets(5, 0, 5, 0));
                    card.getChildren().add(separator);

                    if (reponse != null) {
                        Label responseLabel = new Label("📨 Response: " + reponse.getContenuReponse());
                        responseLabel.getStyleClass().add("reclamation-description");
                        responseLabel.setWrapText(true);
                        card.getChildren().add(responseLabel);
                    } else {
                        Label awaitingLabel = new Label("⏳ Awaiting response from admin");
                        awaitingLabel.getStyleClass().add("reclamation-status");
                        awaitingLabel.setStyle("-fx-font-style: italic;");
                        card.getChildren().add(awaitingLabel);
                    }

                    setGraphic(card);
                }
            }
        };
    }

    @FXML
    private void handleProfileNavigation(ActionEvent event) {
        navigateToWithUserData("/view/profile.fxml", event);
    }

    @FXML
    private void handleViewReclamationNavigation(ActionEvent event) {
        navigateToWithUserData("/view/view_reclamation.fxml", event);
    }

    @FXML
    private void handleViewResponsesNavigation(ActionEvent event) {
    }

    private void navigateToWithUserData(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();

            if (fxmlFile.equals("/view/view_reclamation.fxml")) {
                ViewReclamationController viewReclamationController = loader.getController();
                viewReclamationController.setUser(user);
            } else if (fxmlFile.equals("/view/profile.fxml")) {
                ProfileController profileController = loader.getController();
                profileController.setUserData(user);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(page));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
