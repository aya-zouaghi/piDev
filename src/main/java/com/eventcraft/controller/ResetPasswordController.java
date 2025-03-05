package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;
import java.io.IOException;

public class ResetPasswordController {
    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Button resetButton;

    private User user; // Store the user object for password update

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    public void handleResetPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        UserDAO userDAO = new UserDAO();

        if (user != null && !newPassword.isEmpty()) {
            boolean isUpdated = userDAO.updatePassword(user, newPassword);
            if (isUpdated) {
                // Show success message
                showAlert("Success", "Password is updated successfully!", Alert.AlertType.INFORMATION);

                // Navigate to login.fxml
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to load login page.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "Failed to update password. Please try again.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Warning", "Password cannot be empty!", Alert.AlertType.WARNING);
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
