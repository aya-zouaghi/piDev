package com.eventcraft.controller;

import com.eventcraft.model.Reservationsalle;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import com.eventcraft.service.ReservationSalleService;
import com.eventcraft.model.Salle;
import javafx.scene.control.ComboBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatistiquesalleController {

    @FXML
    private BarChart<String, Number> reservationChart;

    @FXML
    private ComboBox<String> periodChoice; // ComboBox pour choisir la période

    private Salle salle;
    private ReservationSalleService reservationService = new ReservationSalleService();

    public void setSalle(Salle salle) {
        this.salle = salle;
        initializePeriodChoice(); // Initialiser le ComboBox
        loadStatistics(); // Charger les statistiques par défaut
    }

    // Initialiser le ComboBox avec les options
    private void initializePeriodChoice() {
        periodChoice.getItems().addAll("Mois", "Semaine");
        periodChoice.setValue("Mois"); // Valeur par défaut

        // Écouter les changements de sélection
        periodChoice.setOnAction(event -> loadStatistics());
    }

    private void loadStatistics() {
        try {
            List<Reservationsalle> reservations = reservationService.getReservationsBySalleId(salle.getIdSalle());

            // Effacer les données précédentes du graphique
            reservationChart.getData().clear();

            // Créer une série de données pour le graphique
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            // Déterminer le mode d'affichage (mois ou semaine)
            String selectedPeriod = periodChoice.getValue();
            if ("Mois".equals(selectedPeriod)) {
                series.setName("Réservations par Mois");
                Map<String, Long> reservationsByMonth = reservations.stream()
                        .collect(Collectors.groupingBy(
                                reservation -> reservation.getDate_debut().toLocalDate().getMonth().toString(),
                                Collectors.counting()
                        ));
                reservationsByMonth.forEach((month, count) -> {
                    series.getData().add(new XYChart.Data<>(month, count));
                });
            } else if ("Semaine".equals(selectedPeriod)) {
                series.setName("Réservations par Semaine");
                Map<String, Long> reservationsByWeek = reservations.stream()
                        .collect(Collectors.groupingBy(
                                reservation -> {
                                    LocalDate date = reservation.getDate_debut().toLocalDate();
                                    int weekNumber = date.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
                                    return date.getYear() + "-S" + weekNumber;
                                },
                                Collectors.counting()
                        ));
                reservationsByWeek.forEach((week, count) -> {
                    series.getData().add(new XYChart.Data<>(week, count));
                });
            }

            // Ajouter la série au graphique
            reservationChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur
        }
    }
}