package services;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
public class ServiceStatistiqueCommande {
    private Connection connection  = MyDatabase.getInstance().getConnection();

    public int getNombreTotalCommandes() throws SQLException {
        String query = "SELECT COUNT(*) FROM commande_decoration";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int totalCommandes = rs.getInt(1);
                System.out.println("Nombre total de commandes récupéré : " + totalCommandes);
                return totalCommandes;
            }
        }
        System.out.println("Aucune commande trouvée.");
        return 0;
    }

    public Map<String, Integer> getNbrCommandesParDecoration() throws SQLException {
        Map<String, Integer> commandesParDecoration = new HashMap<>();
        String query = "SELECT decoration, COUNT(*) AS nbr_commandes FROM commande_decoration GROUP BY decoration";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String decoration = resultSet.getString("decoration");
                int nbrCommandes = resultSet.getInt("nbr_commandes");
                commandesParDecoration.put(decoration, nbrCommandes);
            }
        }

        return commandesParDecoration;
    }



    // Calcul du chiffre d'affaires total
    public double getChiffreAffairesTotal() throws SQLException {
        String query = "SELECT SUM(prix) FROM commande_decoration";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                double chiffreAffaires = rs.getDouble(1);
                System.out.println("Chiffre d'affaires total récupéré : " + chiffreAffaires + " DT");
                return chiffreAffaires;
            }
        }
        System.out.println("Aucun chiffre d'affaires trouvé.");
        return 0.0;
    }

    // Statistiques mensuelles (nombre de commandes par mois)
    public Map<String, Integer> getCommandesParMois() throws SQLException {
        String query = """
            SELECT DATE_FORMAT(date_commande, '%Y-%m') AS mois, COUNT(*) AS nombre_commandes
            FROM commande_decoration
            GROUP BY mois
            ORDER BY mois;
            """;
        Map<String, Integer> stats = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String mois = rs.getString("mois");
                int nombreCommandes = rs.getInt("nombre_commandes");
                System.out.println("Mois : " + mois + ", Nombre de commandes : " + nombreCommandes);
                stats.put(mois, nombreCommandes);
            }
        }
        if (stats.isEmpty()) {
            System.out.println("Aucune commande par mois trouvée.");
        }
        return stats;
    }
}
