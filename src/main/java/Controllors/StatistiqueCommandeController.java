package Controllors;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import services.ServiceStatistiqueCommande;

import java.sql.SQLException;
import java.util.Map;

public class StatistiqueCommandeController {
    @FXML
    private Label totalCommandesLabel;

    @FXML
    private Label chiffreAffairesLabel;

    @FXML
    private BarChart<String, Number> statistiquesChart;

    @FXML
    private Button statMoisButton;

    @FXML
    private Button statDecoButton;

    private ServiceStatistiqueCommande serviceStatistique = new ServiceStatistiqueCommande();

    @FXML
    public void initialize() {
        chargerStatistiques(); // Affiche les stats mensuelles par défaut

        statMoisButton.setOnAction(event -> chargerStatistiques());
        statDecoButton.setOnAction(event -> chargerStatistiquesDecorations());
    }

    private void chargerStatistiquesDecorations() {
        try {
            Map<String, Integer> commandesParDecoration = serviceStatistique.getNbrCommandesParDecoration();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Commandes par décoration");

            statistiquesChart.getData().clear();
            for (Map.Entry<String, Integer> entry : commandesParDecoration.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            statistiquesChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerStatistiques() {
        try {
            int totalCommandes = serviceStatistique.getNombreTotalCommandes();
            totalCommandesLabel.setText("Total Commandes : " + totalCommandes);

            double chiffreAffaires = serviceStatistique.getChiffreAffairesTotal();
            chiffreAffairesLabel.setText("Chiffre d'affaires : " + chiffreAffaires + " DT");

            Map<String, Integer> commandesParMois = serviceStatistique.getCommandesParMois();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Commandes par mois");

            statistiquesChart.getData().clear();
            for (Map.Entry<String, Integer> entry : commandesParMois.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            statistiquesChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
