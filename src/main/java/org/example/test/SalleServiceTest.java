package org.example.test;

import SERVICE.SalleService;
import org.example.entities.Salle;

import java.sql.SQLException;
import java.util.List;

public class SalleServiceTest {
    public static void main(String[] args) {
        SalleService salleService = new SalleService();

        try {
            // Ajout de salles pour tester les réservations
            Salle salle1 = new Salle(0, "Salle A", 50, "Projecteur, WiFi", "salleA.jpg", "Tunis", 1, "Superbe", 150); // Suppression de la disponibilité
            Salle salle2 = new Salle(0, "Salle B", 100, "Micro, WiFi", "salleB.jpg", "Sousse", 1, "Exceptionnel", 250); // Suppression de la disponibilité

            // Ajout des salles
            salleService.ajouter(salle1);
            salleService.ajouter(salle2);
            System.out.println("Salles ajoutées avec succès !");

            // Récupération des salles pour s'assurer qu'elles existent
            List<Salle> salles = salleService.afficher();
            if (salles.isEmpty()) {
                System.out.println("Aucune salle trouvée !");
                return;
            }

            // Mise à jour de la salle "Salle A"
            Salle salleAMajoree = salles.get(0); // Supposons que la première salle soit "Salle A"
            salleAMajoree.setNomSalle("Salle A - Mise à jour");
            salleAMajoree.setCapacite(60); // Augmenter la capacité
            salleAMajoree.setEquipement("Projecteur, WiFi, Table de conférence");
            // salleAMajoree.setDisponibilite(false); // Ne plus modifier la disponibilité
            salleAMajoree.setImageSalle("salleA_updated.jpg");
            salleAMajoree.setLocationSalle("Tunis - Centre");
            salleAMajoree.setQualite("Fabuleuse"); // Mise à jour de la qualité
            salleAMajoree.setPrix(200); // Mise à jour du prix

            // Appel à la méthode de mise à jour
            salleService.modifier(salleAMajoree);
            System.out.println("Salle A mise à jour avec succès !");

            // Vérification des données mises à jour
            List<Salle> sallesMisesAJour = salleService.afficher();
            for (Salle s : sallesMisesAJour) {
                System.out.println(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
