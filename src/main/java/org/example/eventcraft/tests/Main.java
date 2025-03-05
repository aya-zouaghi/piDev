package org.example.eventcraft.tests;

import org.example.eventcraft.SERVICE.CrudDemande;
import org.example.eventcraft.controllers.Sms;
import org.example.eventcraft.entites.DemandeOffre;

import java.sql.SQLException;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        // Create an instance of CrudDemande
        CrudDemande crudDemande = new CrudDemande();

        try {
            // Display all demandes before modification
            System.out.println("Before modification:");
            for (DemandeOffre d : crudDemande.afficher()) {
                System.out.println(d);
            }

            // Modify an existing demande (Assume id = 1 exists)
            int idToModify = 2; // Change this to an existing ID in your database
            DemandeOffre updatedDemande = new DemandeOffre(idToModify, 1, 7, "nice", new Date());

            System.out.println("Modifying demande with ID: " + idToModify);
            crudDemande.modifier(updatedDemande);

            // Display all demandes after modification
            System.out.println("After modification:");
            for (DemandeOffre d : crudDemande.afficher()) {
                System.out.println(d);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }



}
