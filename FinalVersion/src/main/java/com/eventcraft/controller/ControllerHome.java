package com.eventcraft.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.eventcraft.model.User;
import com.eventcraft.model.Reclamation;
import com.eventcraft.dao.DashDAO;
import com.eventcraft.dao.UserDAO;
import javafx.geometry.Insets;

public class ControllerHome implements Initializable {

   

    @FXML
    private VBox pnItems;

    @FXML
    private Button btnOverview;

    @FXML
    private Button btnOrders;

    @FXML
    private Button btnCustomers;

    @FXML
    private Button btnMenus;

    @FXML
    private Button btnPackages;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnSignout;

    @FXML
    private Pane pnlCustomer;

    @FXML
    private Pane pnlOrders;

    @FXML
    private Pane pnlOverview;

    @FXML
    private Pane pnlMenus;

    @FXML
    private ListView<User> userListView;

    @FXML
    private ListView<Reclamation> reclamationListView;

    @FXML
    private TextField searchField;

    private ObservableList<User> userList;
    private ObservableList<Reclamation> reclamationList;
    private DashDAO dashDAO;
    private UserDAO userDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList = FXCollections.observableArrayList();
        reclamationList = FXCollections.observableArrayList();
        userListView.setItems(userList);
        reclamationListView.setItems(reclamationList);
        userListView.setCellFactory(this::createUserListCell);
        reclamationListView.setCellFactory(this::createReclamationListCell);
        dashDAO = new DashDAO();
        userDAO = new UserDAO();
        loadUserData();

        // Wrap the ObservableList in a FilteredList and bind to the search field
        FilteredList<User> filteredList = new FilteredList<>(userList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                        user.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                        user.getEmail().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<User> sortedList = new SortedList<>(filteredList);
        userListView.setItems(sortedList);
    }

    private void loadUserData() {
        List<User> users = dashDAO.getAllUsers();
        userList.setAll(users);
    }

    private void loadReclamationData() {
        List<Reclamation> reclamations = dashDAO.getAllReclamations();
        reclamationList.setAll(reclamations);
    }

    private ListCell<User> createUserListCell(ListView<User> listView) {
        return new ListCell<User>() {
            private final Button banButton = new Button();

            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Set button text based on user status
                    if ("Banned".equalsIgnoreCase(user.getStatutCompte())) {
                        banButton.setText("🚫 Unban");
                    } else {
                        banButton.setText("🚫 Ban");
                    }
                    banButton.setOnAction(event -> handleBanButton(user, banButton));
                    banButton.getStyleClass().add("button");

                    if ("admin".equalsIgnoreCase(user.getRole())) {
                        banButton.setDisable(true);
                    }

                    Label userDetails = new Label(String.format(
                            "🆔 ID: %d\n👤 Name: %s %s\n📧 Email: %s\n🔑 Password: %s\n🔒 Status: %s\n🏷️ Role: %s",
                            user.getIdUser(),
                            user.getNom(),
                            user.getPrenom(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getStatutCompte(),
                            user.getRole()
                    ));
                    userDetails.getStyleClass().add("label");

                    HBox hbox = new HBox(userDetails, banButton);
                    hbox.setSpacing(10);
                    hbox.setPadding(new Insets(5));
                    setGraphic(hbox);
                    getStyleClass().add("list-cell");
                }
            }
        };
    }

    private ListCell<Reclamation> createReclamationListCell(ListView<Reclamation> listView) {
        return new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Button reponseButton = new Button("🔄 Reponse");
                    reponseButton.setOnAction(event -> handleReponseButton(reclamation));
                    reponseButton.getStyleClass().add("button");

                    // Get user name from reclamation's user ID
                    String userName = getUserNameById(reclamation.getIdUser());

                    Label reclamationDetails = new Label(String.format(
                            "📜 Title: %s\n📄 Description: %s\n📅 Date: %s\n🔒 Status: %s\n🏷️ Type: %s\n👤 From: %s",
                            reclamation.getTitre(),
                            reclamation.getDescription(),
                            reclamation.getDate(),
                            reclamation.getStatut(),
                            reclamation.getType(),
                            userName  // Added user name
                    ));
                    reclamationDetails.getStyleClass().add("label");

                    HBox hbox = new HBox(reclamationDetails, reponseButton);
                    hbox.setSpacing(10);
                    hbox.setPadding(new Insets(5));
                    setGraphic(hbox);
                    getStyleClass().add("list-cell");
                }
            }
        };
    }

    // Helper method to get user name by ID
    private String getUserNameById(int userId) {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            return user.getNom() + " " + user.getPrenom();
        }
        return "Unknown User";
    }

    private void handleBanButton(User user, Button banButton) {
        String newStatus = "Banned".equalsIgnoreCase(user.getStatutCompte()) ? "Active" : "Banned";
        user.setStatutCompte(newStatus);

        if (UserDAO.updateUser(user)) {
            // Update button text based on new status
            banButton.setText(newStatus.equalsIgnoreCase("Banned") ? "🚫 Unban" : "🚫 Ban");
            loadUserData();
            System.out.println("User status updated successfully: " + user.getStatutCompte());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to update user status");
            alert.setContentText("There was an error updating the user's status. Please try again.");
            alert.showAndWait();
            System.err.println("Failed to update user status.");
        }
    }

    private void handleDeleteReclamationButton(Reclamation reclamation) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Deletion");
        confirmationDialog.setHeaderText("Are you sure you want to delete this reclamation?");
        confirmationDialog.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dashDAO.deleteReclamation(reclamation.getId());
            loadReclamationData();
        }
    }

    public void handleClicks(ActionEvent actionEvent) throws IOException {
        if (actionEvent.getSource() == btnCustomers) {
            pnlCustomer.setStyle("-fx-background-color : #1620A1");
            pnlCustomer.toFront();
        }
        if (actionEvent.getSource() == btnMenus) {
            pnlMenus.setStyle("-fx-background-color : #53639F");
            pnlMenus.toFront();
        }
        if (actionEvent.getSource() == btnOverview) {
            pnlOverview.setStyle("-fx-background-color : #02030A");
            pnlOverview.toFront();
            loadUserData();
        }
        if (actionEvent.getSource() == btnOrders) {
            pnlOrders.setStyle("-fx-background-color : #464F67");
            pnlOrders.toFront();
            loadReclamationData();
        }
        if (actionEvent.getSource() == btnCustomers) {
            // Load profile.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the stage information
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    private void handleReponseButton(Reclamation reclamation) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Response");
        dialog.setHeaderText("Enter your response to the reclamation:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextArea responseField = new TextArea();
        responseField.setPromptText("Response");

        VBox form = new VBox(responseField);
        form.setSpacing(10);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return responseField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(response -> {
            dashDAO.saveReponse(reclamation.getId(), response);
            loadReclamationData();
        });
    }
}
