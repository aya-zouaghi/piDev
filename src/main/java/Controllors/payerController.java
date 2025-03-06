package Controllors;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import entities.Payment;
import services.APIpayment;
import com.stripe.model.PaymentIntent;
import services.ServicePayment;

import java.io.IOException;
import java.sql.SQLException;

public class payerController {
    @FXML private TextField montant;
    @FXML private TextField emailField;
    @FXML private TextField card_id;
    @FXML private Text error1;

    private boolean isUpdating = false; // Prevents recursive updates
    @FXML
    private Button Valider;
    @FXML
    private Button afficherlist;


    // ✅ Constructor
    public payerController() {
    }

    @FXML
    public void initialize() {


        montant.setText("0.00");

        if (emailField != null) {
            emailField.setDisable(false);
            emailField.setEditable(true);
        }
    }
    public void setMontant(double montantValue) {
        montant.setText(String.format("%.2f", montantValue)); // Formatage en 2 décimales
    }

   @FXML
    void Valider(ActionEvent event) {
        try {
            error1.setText("");

            double amount = Double.parseDouble(montant.getText().replace(",", "."));
            String cardNumber = card_id.getText().trim();
            String userEmail = emailField.getText().trim();



            // ✅ Vérification du numéro de carte (XXXX XXXX XXXX XXXX ou XXXX-XXXX-XXXX-XXXX)
            if (!cardNumber.matches("^(\\d{4}[ -]){3}\\d{4}$")) {
                error1.setText("Numéro de carte invalide. Utilisez XXXX XXXX XXXX XXXX ou XXXX-XXXX-XXXX-XXXX.");
                return;
            }

            // ✅ Vérification de l'adresse email
            if (!userEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                error1.setText("Adresse e-mail invalide.");
                return;
            }

            // ✅ Vérification du montant
            if (amount <= 0) {
                error1.setText("Montant invalide.");
                return;
            }



            //✅ Création du paiement
            PaymentIntent paymentIntent = APIpayment.createPayment(amount);

            if (paymentIntent != null) {
                String paymentIntentId = paymentIntent.getId();

                Payment paiement = new Payment();
                paiement.setAmount(amount);
                paiement.setTransactionId(paymentIntentId);
                paiement.setEmail(userEmail);

                ServicePayment servicePayment = new ServicePayment();
                servicePayment.ajouter(paiement);
                System.out.println("✅ Email enregistré dans la base : " + userEmail);


                showAlert("⚠ Paiement effectue avec succes.");


            } else {
                showAlert("⚠ Paiement échoué.");
            }
        } catch (NumberFormatException e) {
            showAlert("⚠ Montant invalide.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

       try{
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommande.fxml"));
           Parent root = loader.load();

           // Remplacer la scène actuelle avec la nouvelle page
           Stage stage = (Stage) afficherlist.getScene().getWindow();
           stage.setScene(new javafx.scene.Scene(root));
           stage.show();
       } catch (IOException e) {
           e.printStackTrace();
           showAlert("Erreur", "Impossible de retourner à la liste des commandes.", Alert.AlertType.ERROR);
       }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    @FXML
    public void AfficherCommande(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommande.fxml"));
            Parent root = loader.load();

            // Remplacer la scène actuelle avec la nouvelle page
            Stage stage = (Stage) afficherlist.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des commandes.", Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setWidth(500);
        alert.setHeight(500);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void retourPagePrecedente(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close(); // Ferme la fenêtre actuelle
    }
}