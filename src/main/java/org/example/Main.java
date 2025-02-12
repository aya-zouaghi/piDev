package org.example;

import org.example.entites.Offre;
import SERVICE.Ioffre;
import SERVICE.Crudoffre;

import SERVICE.CrudDemande;
import org.example.entites.DemandeOffre;
import java.sql.SQLException;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Crudoffre serviceOffre = new Crudoffre();

        // Assurez-vous que chaque offre a un ID unique
        Offre offre1 = new Offre("Offre 1", "Description 1", "Type 1", 150.0f, new Date(), 1, null);

        try {
            serviceOffre.ajouter(offre1);
            System.out.println("Offres ajoutées avec succès!");
            System.out.println(serviceOffre.afficher());
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        CrudDemande serviceDemande = new CrudDemande();

        // Créer une nouvelle demande
        DemandeOffre demande1 = new DemandeOffre(1, 1, "En attente", new Date());

        try {
            serviceDemande.ajouter(demande1);
            System.out.println("Demande ajoutée avec succès !");
            System.out.println(serviceDemande.afficher());
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}


