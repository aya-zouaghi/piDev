package org.example.controller;


import SERVICE.SalleService;
import org.example.entities.Salle;

import java.sql.SQLException;
import java.util.List;

public class SalleController {
    private SalleService salleService = new SalleService();

    // Afficher toutes les salles
    public void afficherSalles() {
        try {
            List<Salle> salles = salleService.afficher();
            for (Salle s : salles) {
                // Affichage des détails des salles dans la console
                System.out.println("Salle: " + s.getNomSalle() +
                        ", Capacité: " + s.getCapacite() +
                        ", Équipement: " + s.getEquipement() +
                        ", Location: " + s.getLocationSalle());
            }
        } catch (SQLException e) {
            // Gérer les erreurs SQL
            e.printStackTrace();
        }
    }

    // Ajouter une salle
    public void ajouterSalle(Salle salle) {
        try {
            salleService.ajouter(salle);
            System.out.println("Salle ajoutée avec succès!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier une salle
    public void modifierSalle(Salle salle) {
        try {
            salleService.modifier(salle);
            System.out.println("Salle modifiée avec succès!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer une salle
    public void supprimerSalle(int idSalle) {
        try {
            salleService.supprimer(idSalle);
            System.out.println("Salle supprimée avec succès!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
