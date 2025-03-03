package com.eventcraft.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import com.eventcraft.model.User;
import com.eventcraft.model.Reclamation;
import com.eventcraft.dao.DashDAO;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList = FXCollections.observableArrayList();
        reclamationList = FXCollections.observableArrayList();
        userListView.setItems(userList);
        reclamationListView.setItems(reclamationList);
        userListView.setCellFactory(this::createUserListCell);
        reclamationListView.setCellFactory(this::createReclamationListCell);
        dashDAO = new DashDAO();
        loadUserData();
    }

    private void loadUserData() {
        // Fetch user data from the DashDAO class
        List<User> users = dashDAO.getAllUsers();

        // Populate the ListView with user data
        userList.setAll(users);
    }

    private void loadReclamationData() {
        // Fetch all reclamation data from the DashDAO class
        List<Reclamation> reclamations = dashDAO.getAllReclamations();

        // Populate the ListView with reclamation data
        reclamationList.setAll(reclamations);
    }

    private ListCell<User> createUserListCell(ListView<User> listView) {
        return new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Button updateButton = new Button("Update");
                    updateButton.setOnAction(event -> handleUpdateButton(user));

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setOnAction(event -> handleDeleteButton(user));

                    // Disable delete button for admin users
                    if ("admin".equalsIgnoreCase(user.getRole())) {
                        deleteButton.setDisable(true);
                    }

                    Label userDetails = new Label(String.format(
                            "ID: %d\nName: %s %s\nEmail: %s\nPassword: %s\nStatus: %s\nRole: %s",
                            user.getIdUser(),
                            user.getNom(),
                            user.getPrenom(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getStatutCompte(),
                            user.getRole()
                    ));

                    HBox hbox = new HBox(userDetails, updateButton, deleteButton);
                    hbox.setSpacing(10);
                    setGraphic(hbox);
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
                    Button updateButton = new Button("Update");
                    updateButton.setOnAction(event -> handleUpdateReclamationButton(reclamation));

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setOnAction(event -> handleDeleteReclamationButton(reclamation));

                    Label reclamationDetails = new Label(String.format(
                            "Title: %s\nDescription: %s\nDate: %s\nStatus: %s\nType: %s",
                            reclamation.getTitre(),
                            reclamation.getDescription(),
                            reclamation.getDate(),
                            reclamation.getStatut(),
                            reclamation.getType()
                    ));

                    HBox hbox = new HBox(reclamationDetails, updateButton, deleteButton);
                    hbox.setSpacing(10);
                    setGraphic(hbox);
                }
            }
        };
    }

    private void handleUpdateButton(User user) {
        // Create a dialog to update user data
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Update User Data");
        dialog.setHeaderText("Enter the new data for the user:");

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField nomField = new TextField(user.getNom());
        nomField.setPromptText("First Name");
        TextField prenomField = new TextField(user.getPrenom());
        prenomField.setPromptText("Last Name");
        TextField emailField = new TextField(user.getEmail());
        emailField.setPromptText("Email");
        TextField passwordField = new TextField(user.getPassword());
        passwordField.setPromptText("Password");
        TextField statutCompteField = new TextField(user.getStatutCompte());
        statutCompteField.setPromptText("Account Status");
        TextField roleField = new TextField(user.getRole());
        roleField.setPromptText("Role");

        // Add the fields to a grid
        VBox form = new VBox(nomField, prenomField, emailField, passwordField, statutCompteField, roleField);
        form.setSpacing(10);
        dialog.getDialogPane().setContent(form);

        // Convert the result to a user object when the update button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                user.setNom(nomField.getText());
                user.setPrenom(prenomField.getText());
                user.setEmail(emailField.getText());
                user.setPassword(passwordField.getText());
                user.setStatutCompte(statutCompteField.getText());
                user.setRole(roleField.getText());
                return user;
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        Optional<User> result = dialog.showAndWait();
        result.ifPresent(updatedUser -> {
            // Update the user in the database
            dashDAO.updateUser(updatedUser);
            // Refresh the list view
            loadUserData();
        });
    }

    private void handleDeleteButton(User user) {
        // Confirm deletion with the user
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Deletion");
        confirmationDialog.setHeaderText("Are you sure you want to delete this user?");
        confirmationDialog.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete the user from the database
            dashDAO.deleteUser(user.getIdUser());
            // Refresh the list view
            loadUserData();
        }
    }

    private void handleUpdateReclamationButton(Reclamation reclamation) {
        // Create a dialog to update reclamation data
        Dialog<Reclamation> dialog = new Dialog<>();
        dialog.setTitle("Update Reclamation Data");
        dialog.setHeaderText("Enter the new data for the reclamation:");

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField titrField = new TextField(reclamation.getTitre());
        titrField.setPromptText("Title");
        TextField descriptionField = new TextField(reclamation.getDescription());
        descriptionField.setPromptText("Description");
        TextField dateField = new TextField(reclamation.getDate().toString());
        dateField.setPromptText("Date");
        TextField statutField = new TextField(reclamation.getStatut());
        statutField.setPromptText("Status");
        TextField typeField = new TextField(reclamation.getType());
        typeField.setPromptText("Type");

        // Add the fields to a grid
        VBox form = new VBox(titrField, descriptionField, dateField, statutField, typeField);
        form.setSpacing(10);
        dialog.getDialogPane().setContent(form);

        // Convert the result to a reclamation object when the update button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                reclamation.setTitre(titrField.getText());
                reclamation.setDescription(descriptionField.getText());
                reclamation.setDate(java.time.LocalDateTime.parse(dateField.getText()));
                reclamation.setStatut(statutField.getText());
                reclamation.setType(typeField.getText());
                return reclamation;
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        Optional<Reclamation> result = dialog.showAndWait();
        result.ifPresent(updatedReclamation -> {
            // Update the reclamation in the database
            dashDAO.updateReclamation(updatedReclamation);
            // Refresh the list view
            loadReclamationData();
        });
    }

    private void handleDeleteReclamationButton(Reclamation reclamation) {
        // Confirm deletion with the user
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Deletion");
        confirmationDialog.setHeaderText("Are you sure you want to delete this reclamation?");
        confirmationDialog.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete the reclamation from the database
            dashDAO.deleteReclamation(reclamation.getId());
            // Refresh the list view
            loadReclamationData();
        }
    }

    public void handleClicks(ActionEvent actionEvent) {
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
            loadUserData(); // Reload user data when Overview is clicked
        }
        if (actionEvent.getSource() == btnOrders) {
            pnlOrders.setStyle("-fx-background-color : #464F67");
            pnlOrders.toFront();
            loadReclamationData(); // Load reclamation data when Reclamation is clicked
        }
    }
}
