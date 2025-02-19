package tests;

import entities.CommandeDecoration;

import entities.Decoration;
import entities.Evenement;
import services.ServiceCommandeDeco;
import services.ServiceDecoration;
import services.ServiceEvenement;
import services.ServiceUtilisateur;

import java.sql.SQLException;
import java.time.LocalDate;

import entities.Utilisateur;
public class Main {
    public static void main(String[] args) {
        ServiceCommandeDeco CommandeDeco = new ServiceCommandeDeco();
        ServiceDecoration deco=new ServiceDecoration();
        ServiceUtilisateur sp = new ServiceUtilisateur();
        ServiceEvenement serviceEvenement = new ServiceEvenement();

        try {
                                                // crud decoration
            int idUtilisateurExist = 2;
            Utilisateur util = sp.getUtilisateurById(idUtilisateurExist);
            int idDecoExist = 2;
            Decoration decoration = deco.getDecorationById(idDecoExist);
           // Utilisateur util = new Utilisateur(2, "Ahmed", "Taboubi","123","active","client","");
           if (util !=  null) {
            //   deco.ajouter(new Decoration("table", "des tables", "des grandes tables", 10, 1000, util));
             //  deco.ajouter(new Decoration("chaise", "des chaises", "des chaises plastiques", 5, 20000, util));
            //deco.modifier(new Decoration(11,"fleurs", "des fleurs", "des petites fleurs rouges", 20, 7000,util));


            }else{System.out.println("utilisteur n'existe pas");}
           //deco.supprimer(5);
            // System.out.println(sp.afficher());
            // System.out.println(deco.afficher());


                                                    // crud commande deco

            Evenement evenement = serviceEvenement.getEvenementById(2);

            if (evenement != null && decoration != null) {
                CommandeDeco.ajouter(new CommandeDecoration(15, LocalDate.now(), 5000.0f, evenement, decoration));
                //CommandeDeco.modifier(new CommandeDecoration(3,11,LocalDate.now(), 6000.0f, evenement, decoration));
            }
                 //CommandeDeco.supprimer(4);
             System.out.println(CommandeDeco.afficher());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        }
    }





