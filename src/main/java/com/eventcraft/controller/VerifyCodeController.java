package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;

import java.io.IOException;

public class VerifyCodeController {
    @FXML
    private TextField codeField;
    @FXML
    private Button verifyButton;

    private String expectedCode; // Store the expected verification code
    private String email; // Store the email for user identification

    public void setExpectedCode(String expectedCode) {
        this.expectedCode = expectedCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void handleVerifyCode() {
        String enteredCode = codeField.getText();
        if (expectedCode.equals(enteredCode)) {
            // Show success message
            Alert successAlert = new Alert(AlertType.INFORMATION);
            successAlert.setTitle("Verification Successful");
            successAlert.setHeaderText(null);
            successAlert.setContentText("The code is verified.");
            successAlert.showAndWait();

            // Navigate to the password reset screen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reset_password.fxml"));
                Parent root = loader.load();
                ResetPasswordController resetPasswordController = loader.getController();

                // Fetch the user object based on the email
                UserDAO userDAO = new UserDAO();
                User user = userDAO.getUserByEmail(email);
                resetPasswordController.setUser(user);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                // Optionally, close the current stage
                Stage currentStage = (Stage) verifyButton.getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
                // Show error message: Failed to load password reset screen
            }
        } else {
            // Show error message
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setTitle("Verification Failed");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("The code is incorrect. Please try again.");
            errorAlert.showAndWait();
        }
    }

}
