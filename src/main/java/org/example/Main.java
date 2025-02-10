package org.example;


import org.example.entities.Commentaire;
import org.example.entities.Forum;
import org.example.services.CommentaireService;
import org.example.services.ForumService;

import java.sql.SQLException;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        ForumService forumService = new ForumService();
        CommentaireService commentaireService = new CommentaireService();

        Commentaire commentaire1 = new Commentaire("This is a great forum!", new Date(), 2, 3);
        Commentaire commentaire2 = new Commentaire(1, "Updated content", new Date(), 1, 1);
        Forum forum1 = new Forum(3,"Title 1", "Description 1", new Date(), 2);
        Forum forum2 = new Forum(1, "Updated Title", "Updated Description", new Date(), 2);

        try {
            // Add a new forum
            forumService.ajouter(forum1);
            System.out.println("Forum ajouté avec succès");
            commentaireService.ajouter(commentaire1);
            System.out.println("Commentaire ajouté avec succès");

            // Modify a forum
            forumService.modifier(forum2);
            System.out.println("Forum modifié avec succès");

            // Display all forums
            System.out.println("Liste des forums : ");
            forumService.afficher().forEach(System.out::println);

            // Delete forum example
            // forumService.supprimer(1);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    }
