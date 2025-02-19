package com.eventcraft.service;

import com.eventcraft.model.DemandeOffre;
import com.eventcraft.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrudDemande implements Idemande {
    private Connection connection = DatabaseConnection.getConnection();

    @Override
    public void ajouter(DemandeOffre demande) throws SQLException {
        // SQL insert query
        String sql = "INSERT INTO demande_offre (user, offre, statut_demande, date_demande) VALUES (?, ?, ?, ?)";

        // Prepare the statement
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, demande.getUser());
        ps.setInt(2, demande.getOffre());
        ps.setString(3, demande.getStatutDemande());
        ps.setDate(4, new Date(demande.getDateDemande().getTime()));

        // Execute update
        ps.executeUpdate();

        // Retrieve generated ID
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            // Set the generated ID to the demande object
            demande.setIdDemande(generatedKeys.getInt(1));
        }
    }

    @Override
    public void modifier(DemandeOffre demande) throws SQLException {
        String sql = "UPDATE demande_offre SET user=?, offre=?, statut_demande=?, date_demande=? WHERE id_demande=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, demande.getUser());
        ps.setInt(2, demande.getOffre());
        ps.setString(3, demande.getStatutDemande());
        ps.setDate(4, new Date(demande.getDateDemande().getTime()));
        ps.setInt(5, demande.getIdDemande());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Demande with id " + demande.getIdDemande() + " was successfully updated.");
        } else {
            System.out.println("No demande found with id " + demande.getIdDemande() + ". Nothing was updated.");
        }
    }


    public void supprimer(int idDemande) throws SQLException {
        if (idDemande <= 0) {
            System.out.println("Invalid id_demande: " + idDemande);
            return; // Exit if the ID is invalid
        }

        String query = "DELETE FROM demande_offre WHERE id_demande = ?";
        PreparedStatement preparedStatement = null;

        try {
            // Disable auto-commit to manage transactions manually
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idDemande);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Record with id_demande " + idDemande + " successfully deleted.");
                connection.commit(); // Commit the transaction
            } else {
                System.out.println("No record found with id_demande " + idDemande);
            }

        } catch (SQLException e) {
            // Rollback in case of an error
            if (connection != null) {
                connection.rollback();
            }
            throw e; // Rethrow the exception to handle it at the calling level
        } finally {
            // Ensure the prepared statement is closed
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Re-enable auto-commit
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<DemandeOffre> afficher() throws SQLException {
        List<DemandeOffre> demandes = new ArrayList<>();
        String sql = "SELECT id_demande, user, offre, statut_demande, date_demande FROM demande_offre"; // Include id_demande
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            demandes.add(new DemandeOffre(
                    rs.getInt("id_demande"),
                    rs.getInt("user"),
                    rs.getInt("offre"),
                    rs.getString("statut_demande"),
                    rs.getDate("date_demande")
            ));
        }
        return demandes;
    }
}
