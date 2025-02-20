package com.eventcraft.service;




import com.eventcraft.DatabaseConnection;
import com.eventcraft.model.Evenement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

public class ServiceEvenement implements IService<Evenement> {
    Connection connection;

    public ServiceEvenement() {
        Connection conn = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(Evenement evenement) throws SQLException {
        String sql = "INSERT INTO `evenement` (`titre`, `description_evenement`, `image`, `location`, `date_debut`, `date_fin`) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, evenement.getTitre());
        ps.setString(2, evenement.getDescription_evenement());
        ps.setString(3, evenement.getImage());
        ps.setString(4, evenement.getLocation());
        ps.setDate(5, Date.valueOf(evenement.getDate_debut()));
        ps.setDate(6, Date.valueOf(evenement.getDate_fin()));
        ps.executeUpdate();
        System.out.println("Événement ajouté !");
    }
    @Override
    public void modifier(Evenement evenement) throws SQLException {
        String sql = "UPDATE `evenement` SET `titre`=?, `description_evenement`=?, `image`=?, `location`=?, `date_debut`=?, `date_fin`=? WHERE `id_evenement`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, evenement.getTitre());
        ps.setString(2, evenement.getDescription_evenement());
        ps.setString(3, evenement.getImage());
        ps.setString(4, evenement.getLocation());
        ps.setDate(5, Date.valueOf(evenement.getDate_debut()));
        ps.setDate(6, Date.valueOf(evenement.getDate_fin()));
        ps.setInt(7, evenement.getId_evenement());
        ps.executeUpdate();
        System.out.println("Événement modifié !");
    }

    @Override
    public void supprimer(int id_evenement) throws SQLException {
        String sql = "DELETE FROM `evenement` WHERE `id_evenement`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id_evenement);
        ps.executeUpdate();
        System.out.println("Événement supprimé !");
    }
    @Override
    public List<Evenement> afficher() throws SQLException {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM `evenement`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            evenements.add(new Evenement(
                    rs.getInt("id_evenement"),
                    rs.getString("titre"),
                    rs.getString("description_evenement"),
                    rs.getString("image"),
                    rs.getString("location"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate()
            ));
        }
        return evenements;
    }
    public Evenement getEvenementById(int id) throws SQLException {
        String query = "SELECT * FROM evenement WHERE id_evenement = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Evenement(
                        resultSet.getInt("id_evenement"),
                        resultSet.getString("titre"),
                        resultSet.getString("description_evenement"),
                        resultSet.getString("image"),
                        resultSet.getString("location"),
                        resultSet.getDate("date_debut").toLocalDate(),
                        resultSet.getDate("date_fin").toLocalDate()
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return null;
    }
}