package org.example.eventcraft.SERVICE;

import org.example.eventcraft.entites.Offre;
import org.example.eventcraft.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Crudoffre implements Ioffre {
    private Connection connection = MyDataBase.getInstance().getConnection();

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
            String sql = "INSERT INTO offre(id_offre, titre_offre, description_offre, type_offre, montant, date_exp, evenement, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "UPDATE offre SET titre_offre=?, description_offre=?, type_offre=?, montant=?, date_exp=?, evenement=?, user_id=?, rating=? WHERE id_offre=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, offre.getTitreOffre());
            ps.setString(2, offre.getDescriptionOffre());
            ps.setString(3, offre.getTypeOffre());
            ps.setFloat(4, offre.getMontant());
            ps.setDate(5, new java.sql.Date(offre.getDateExp().getTime()));
            ps.setInt(6, offre.getEvenement());
            ps.setObject(7, offre.getUser());
            ps.setDouble(8, offre.getRating()); // Add rating to the update query
            ps.setInt(9, offre.getIdOffre());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No rows updated, check if the offer ID exists.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating offer: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(int idOffre) throws SQLException {
        String sql = "DELETE FROM offre WHERE id_offre=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idOffre);
            ps.executeUpdate();
        }
    }
    public void updateOffreRating(Offre offre) throws SQLException {
        String updateQuery = "UPDATE offre SET rating = ? WHERE id_offre = ?";

        // Use the existing connection instead of creating a new one
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setDouble(1, offre.getRating());  // Set the new rating
            statement.setInt(2, offre.getIdOffre());    // Use the offer's ID to update the correct record

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("The rating was updated successfully!");
            } else {
                System.out.println("No offer found with that ID.");
            }
        } catch (SQLException e) {
            // Handle exceptions, e.g. log or rethrow the exception
            e.printStackTrace();
        }
    }

    @Override
    public List<Offre> afficher() throws SQLException {
        List<Offre> offres = new ArrayList<>();
        String sql = "SELECT * FROM offre";
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
                        (Integer) rs.getObject("user_id"),
                        rs.getInt("evenement"),
                        rs.getDouble("rating")
                ));
            }

        }
        return offres;
    }}


