package org.example.test;
import SERVICE.ReservationSalleService;
import org.example.entities.Reservationsalle;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

    public class ReservationSalleServiceTest {
        public static void main(String[] args) {
            ReservationSalleService service = new ReservationSalleService();

            // Création de quelques réservations pour les tests
            Reservationsalle reservation1 = new Reservationsalle(0, 48, 1, Date.valueOf("2025-03-10"), Date.valueOf("2025-03-12"));
            Reservationsalle reservation2 = new Reservationsalle(0, 49, 1, Date.valueOf("2025-04-15"), Date.valueOf("2025-04-16"));

            try {
                // Test de l'ajout d'une réservation
                service.ajouter(reservation1);
                System.out.println("Réservation 1 ajoutée avec succès !");

                // Test de l'ajout de la deuxième réservation
                service.ajouter(reservation2);
                System.out.println("Réservation 2 ajoutée avec succès !");

                // Test de la récupération de toutes les réservations
                List<Reservationsalle> reservations = service.afficher();
                if (reservations.isEmpty()) {
                    System.out.println("Aucune réservation trouvée.");
                } else {
                    System.out.println("Liste des réservations après ajout : ");
                    for (Reservationsalle reservation : reservations) {
                        System.out.println("ID Réservation: " + reservation.getId_reservation() +
                                " | Salle: " + reservation.getSalle() + // Utiliser getSalleId()
                                " | Date début: " + reservation.getDate_debut() +
                                " | Date fin: " + reservation.getDate_fin());
                    }
                }

                // Test de la récupération d'une réservation par ID
                Reservationsalle retrievedReservation = service.getById(reservation1.getId_reservation());
                if (retrievedReservation != null) {
                    System.out.println("Réservation récupérée par ID : " + retrievedReservation.getId_reservation());
                } else {
                    System.out.println("Réservation non trouvée avec l'ID : " + reservation1.getId_reservation());
                }


                // Vérifier la mise à jour
                Reservationsalle updatedReservation = service.getById(reservation1.getId_reservation());
                if (updatedReservation != null) {
                    System.out.println("Réservation après mise à jour : " +
                            "Salle: " + updatedReservation.getSalle() + // Utiliser getSalleId()
                            " | Date début: " + updatedReservation.getDate_debut() +
                            " | Date fin: " + updatedReservation.getDate_fin());
                }

                // Test de la suppression d'une réservation
                service.supprimer(reservation2.getId_reservation());
                System.out.println("Réservation 2 supprimée avec succès !");

                // Vérifier que la réservation a été supprimée
                Reservationsalle deletedReservation = service.getById(reservation2.getId_reservation());
                if (deletedReservation == null) {
                    System.out.println("Réservation 2 supprimée avec succès !");
                } else {
                    System.out.println("Erreur : Réservation 2 non supprimée.");
                }

                // Tester la récupération de toutes les réservations après suppression
                reservations = service.afficher();
                if (reservations.isEmpty()) {
                    System.out.println("Aucune réservation trouvée après suppression.");
                } else {
                    System.out.println("Liste des réservations après suppression : ");
                    for (Reservationsalle reservation : reservations) {
                        System.out.println("ID Réservation: " + reservation.getId_reservation() +
                                " | Salle: " + reservation.getSalle() + // Utiliser getSalleId()
                                " | Date début: " + reservation.getDate_debut() +
                                " | Date fin: " + reservation.getDate_fin());
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

