package com.eventcraft.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.eventcraft.dao.UserDAO;
import com.eventcraft.service.EmailService;
import com.eventcraft.util.TokenGenerator;
import java.io.IOException;

public class ForgotPasswordController {
    @FXML
    private TextField emailField;
    @FXML
    private Button sendCodeButton;

    public void handleSendCode() {
        String email = emailField.getText();
        UserDAO userDAO = new UserDAO();
        if (userDAO.emailExists(email)) {
            String verificationCode = TokenGenerator.generateVerificationCode();
            new EmailService().sendVerificationEmail(email, verificationCode);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/verify_code.fxml"));
                Parent root = loader.load();
                VerifyCodeController verifyCodeController = loader.getController();
                verifyCodeController.setExpectedCode(verificationCode);
                verifyCodeController.setEmail(email); // Pass the email

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                Stage currentStage = (Stage) sendCodeButton.getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Show error message: Email not found
        }
    }


}
