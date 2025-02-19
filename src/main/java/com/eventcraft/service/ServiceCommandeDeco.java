package com.eventcraft.service;

import com.eventcraft.DatabaseConnection;
import com.eventcraft.model.CommandeDecoration;
import com.eventcraft.model.Decoration;
import com.eventcraft.model.Evenement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommandeDeco implements IService<CommandeDecoration> {
    Connection connection;

    public ServiceCommandeDeco() {

        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(CommandeDecoration commandeDecoration) throws SQLException {
        String sql = "INSERT INTO `commande_decoration` (`quantité`, `date_commande`, `prix`, `decoration`) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commandeDecoration.getQuantité());
            ps.setDate(2, Date.valueOf(commandeDecoration.getDate_commande()));
            ps.setFloat(3, commandeDecoration.getPrix());
            ps.setInt(4, commandeDecoration.getDecoration().getId_decor());

            int rowsAffected = ps.executeUpdate();  // Cette ligne renvoie le nombre de lignes affectées
            if (rowsAffected > 0) {
                System.out.println("✅ Commande de décoration ajoutée avec succès !");
            } else {
                System.out.println("❌ Aucun changement dans la base de données.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }
    public void ajouter1(CommandeDecoration commandeDecoration) throws SQLException {
        String sql = "INSERT INTO `commande_decoration` (`quantité`, `date_commande`, `prix`, `evenement_id`, `decoration`) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, commandeDecoration.getQuantité());
        ps.setDate(2, Date.valueOf(commandeDecoration.getDate_commande()));
        ps.setFloat(3, commandeDecoration.getPrix());
        ps.setInt(4, commandeDecoration.getEvenement_id().getId_evenement()); // Supposons que Evenement a une méthode getId_evenement()
        ps.setInt(5, commandeDecoration.getDecoration().getId_decor()); // Supposons que Decoration a une méthode getId_decor()
        ps.executeUpdate();
        System.out.println("Commande de décoration ajoutée");
    }

    @Override
    public void modifier(CommandeDecoration commandeDecoration) throws SQLException {
        // Vérifiez que l'événement et la décoration existent
        String checkEventQuery = "SELECT * FROM evenement WHERE id_evenement = ?";
        PreparedStatement checkEventStmt = connection.prepareStatement(checkEventQuery);
        checkEventStmt.setInt(1, commandeDecoration.getEvenement_id().getId_evenement());
        ResultSet eventResultSet = checkEventStmt.executeQuery();

        String checkDecorationQuery = "SELECT * FROM decoration WHERE id_decor = ?";
        PreparedStatement checkDecorationStmt = connection.prepareStatement(checkDecorationQuery);
        checkDecorationStmt.setInt(1, commandeDecoration.getDecoration().getId_decor());
        ResultSet decorationResultSet = checkDecorationStmt.executeQuery();

        if (!eventResultSet.next() || !decorationResultSet.next()) {
            System.out.println("Erreur : L'événement ou la décoration n'existe pas.");
            return;
        }

        // Mettre à jour la commande de décoration
        String sql = "UPDATE `commande_decoration` SET `quantité`=?, `date_commande`=?, `prix`=?, `evenement_id`=?, `decoration`=? WHERE `id_commande`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, commandeDecoration.getQuantité());
        ps.setDate(2, Date.valueOf(commandeDecoration.getDate_commande()));
        ps.setFloat(3, commandeDecoration.getPrix());
        ps.setInt(4, commandeDecoration.getEvenement_id().getId_evenement());
        ps.setInt(5, commandeDecoration.getDecoration().getId_decor());
        ps.setInt(6, commandeDecoration.getId_commande());
        ps.executeUpdate();
        System.out.println("Commande de décoration modifiée");
    }

    @Override
    public void supprimer(int id_commande) throws SQLException {
        String sql = "DELETE FROM `commande_decoration` WHERE `id_commande`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id_commande);
        ps.executeUpdate();
        System.out.println("Commande de décoration supprimée");
    }

    public List<CommandeDecoration> afficher() throws SQLException {
        List<CommandeDecoration> commandes = new ArrayList<>();
        String sql = "SELECT commande_decoration.id_commande, commande_decoration.quantité, " +
                "commande_decoration.date_commande, commande_decoration.prix, " +
                "decoration.id_decor, decoration.nom_decor, decoration.type_decor, " +
                "decoration.description_decor, decoration.prix, decoration.stock " +
                "FROM commande_decoration " +
                "INNER JOIN decoration ON commande_decoration.decoration = decoration.id_decor";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Decoration decoration = new Decoration(
                        rs.getInt("id_decor"),
                        rs.getString("nom_decor"),
                        rs.getString("type_decor"),
                        rs.getString("description_decor"),
                        rs.getFloat("prix"),
                        rs.getInt("stock"),
                        null // Ajoute l'utilisateur si nécessaire
                );

                CommandeDecoration commande = new CommandeDecoration(
                        rs.getInt("quantité"),
                        rs.getDate("date_commande").toLocalDate(),
                        rs.getFloat("prix"),
                        decoration
                );

                // Ajoute l'ID de la commande ici
                commande.setId_commande(rs.getInt("id_commande"));

                commandes.add(commande);
            }
        }

        return commandes;
    }

    public List<CommandeDecoration> afficher1() throws SQLException {
        List<CommandeDecoration> commandes = new ArrayList<>();
        String sql = "SELECT * FROM `commande_decoration` " +
                "INNER JOIN `evenement` ON commande_decoration.evenement_id = evenement.id_evenement " +
                "INNER JOIN `decoration` ON commande_decoration.decoration = decoration.id_decor";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Evenement evenement = new Evenement(
                    rs.getInt("evenement_id"),
                    rs.getString("titre"),
                    rs.getString("description_evenement"),
                    rs.getString("image"),
                    rs.getString("location"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate()
            );

            Decoration decoration = new Decoration(
                    rs.getInt("id_decor"),
                    rs.getString("nom_decor"),
                    rs.getString("type_decor"),
                    rs.getString("description_decor"),
                    rs.getFloat("prix"),
                    rs.getInt("stock"),
                    null // Vous pouvez ajouter l'utilisateur si nécessaire
            );

            commandes.add(new CommandeDecoration(
                    rs.getInt("id_commande"),
                    rs.getInt("quantité"),
                    rs.getDate("date_commande").toLocalDate(),
                    rs.getFloat("prix"),
                    evenement,
                    decoration
            ));
        }
        return commandes;
    }

    public CommandeDecoration getCommandeDecorationById(int id) throws SQLException {
        String query = "SELECT * FROM commande_decoration WHERE id_commande = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Evenement evenement = new Evenement(
                        resultSet.getInt("evenement_id"),
                        resultSet.getString("titre"),
                        resultSet.getString("description_evenement"),
                        resultSet.getString("image"),
                        resultSet.getString("location"),
                        resultSet.getDate("date_debut").toLocalDate(),
                        resultSet.getDate("date_fin").toLocalDate()
                );

                Decoration decoration = new Decoration(
                        resultSet.getInt("id_decor"),
                        resultSet.getString("nom_decor"),
                        resultSet.getString("type_decor"),
                        resultSet.getString("description_decor"),
                        resultSet.getFloat("prix"),
                        resultSet.getInt("stock"),
                        null // Vous pouvez ajouter l'utilisateur si nécessaire
                );

                return new CommandeDecoration(
                        resultSet.getInt("id_commande"),
                        resultSet.getInt("quantité"),
                        resultSet.getDate("date_commande").toLocalDate(),
                        resultSet.getFloat("prix"),
                        evenement,
                        decoration
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return null;
    }
}