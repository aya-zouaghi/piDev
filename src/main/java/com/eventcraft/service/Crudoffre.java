package com.eventcraft.service;

import com.eventcraft.model.Offre;
import com.eventcraft.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Crudoffre implements org.example.eventcraft.SERVICE.Ioffre {
    private Connection connection = DatabaseConnection.getConnection();

    public Crudoffre() {}

    @Override
    public void ajouter(Offre offre) throws SQLException {
        // Start transaction
        try {
            connection.setAutoCommit(false); // Begin transaction

            // Vérifier si l'événement existe
            String checkEventSql = "SELECT COUNT(*) FROM evenement WHERE id_evenement = ?";
            try (PreparedStatement checkEventStmt = connection.prepareStatement(checkEventSql)) {
                checkEventStmt.setInt(1, offre.getEvenement());
                try (ResultSet eventResult = checkEventStmt.executeQuery()) {
                    if (eventResult.next() && eventResult.getInt(1) == 0) {
                        throw new SQLException("L'événement avec ID " + offre.getEvenement() + " n'existe pas !");
                    }
                }
            }

            // Vérifier le dernier ID inséré et générer un nouvel ID
            String getMaxIdSql = "SELECT MAX(id_offre) FROM offre";
            int newId = 1;
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(getMaxIdSql)) {
                if (rs.next() && rs.getInt(1) != 0) {
                    newId = rs.getInt(1) + 1;
                }
            }

            // Insérer l'offre avec l'ID généré
            String sql = "INSERT INTO `offre`(`id_offre`, `titre_offre`, `description_offre`, `type_offre`, `montant`, `date_exp`, `evenement`, `user_id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, newId);
                ps.setString(2, offre.getTitreOffre());
                ps.setString(3, offre.getDescriptionOffre());
                ps.setString(4, offre.getTypeOffre());
                ps.setFloat(5, offre.getMontant());
                ps.setDate(6, new java.sql.Date(offre.getDateExp().getTime()));
                ps.setInt(7, offre.getEvenement());
                ps.setObject(8, offre.getUser());

                ps.executeUpdate();
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            connection.rollback(); // Rollback if any exception occurs
            throw e; // Rethrow the exception after rollback
        } finally {
            connection.setAutoCommit(true); // Restore the default auto-commit mode
        }
    }

    @Override
    public void modifier(Offre offre) throws SQLException {
        String sql = "UPDATE `offre` SET `titre_offre`=?, `description_offre`=?, `type_offre`=?, `montant`=?, `date_exp`=?, `evenement`=?, `user_id`=? WHERE `id_offre`=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, offre.getTitreOffre());
            ps.setString(2, offre.getDescriptionOffre());
            ps.setString(3, offre.getTypeOffre());
            ps.setFloat(4, offre.getMontant());
            ps.setDate(5, new java.sql.Date(offre.getDateExp().getTime()));
            ps.setInt(6, offre.getEvenement());
            ps.setObject(7, offre.getUser());
            ps.setInt(8, offre.getIdOffre());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int idOffre) throws SQLException {
        String sql = "DELETE FROM `offre` WHERE `id_offre`=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idOffre);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Offre> afficher() throws SQLException {
        List<Offre> offres = new ArrayList<>();
        String sql = "SELECT * FROM `offre`";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                offres.add(new Offre(
                        rs.getInt("id_offre"),
                        rs.getString("titre_offre"),
                        rs.getString("description_offre"),
                        rs.getString("type_offre"),
                        rs.getFloat("montant"),
                        rs.getDate("date_exp"),
                        rs.getInt("evenement"),
                        (Integer) rs.getObject("user_id")
                ));
            }
        }
        return offres;
    }
}
