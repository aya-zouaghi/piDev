package com.eventcraft.service;

import com.eventcraft.DatabaseConnection;
import com.eventcraft.model.Evenement;
import com.eventcraft.util.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {
    private final Connection connection;

    public EvenementService() {
        connection = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(Evenement evenement) throws SQLException {
        int loggedInUserId = SessionManager.getUserId();

        if (loggedInUserId == -1) {
            System.err.println("Erreur: Aucun utilisateur connecté !");
            return;
        }

        String sql = "INSERT INTO evenement (titre, description_evenement, image, date_debut, date_fin, user, location, salle_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, evenement.getTitre());
            ps.setString(2, evenement.getDescription_evenement());
            ps.setString(3, evenement.getImage());
            ps.setDate(4, Date.valueOf(evenement.getDate_debut()));
            ps.setDate(5, Date.valueOf(evenement.getDate_fin()));
            ps.setInt(6, loggedInUserId);
            ps.setString(7, evenement.getLocation());
            ps.setInt(8, 101);

            ps.executeUpdate();
            System.out.println("Evenement ajouté avec succès !");
        }
    }

    @Override
    public void modifier(Evenement evenement) throws SQLException {
        String sql = "UPDATE evenement SET titre=?, description_evenement=?, image=?, date_debut=?, date_fin=?, user=?, location=?, salle_id=? WHERE id_evenement=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, evenement.getTitre());
            ps.setString(2, evenement.getDescription_evenement());
            ps.setString(3, evenement.getImage());
            ps.setDate(4, Date.valueOf(evenement.getDate_debut()));  // Convert LocalDate to SQL Date
            ps.setDate(5, Date.valueOf(evenement.getDate_fin()));    // Convert LocalDate to SQL Date
            ps.setInt(6, evenement.getId_evenement());  // Assuming user is part of the event, or you might need to change this logic.
            ps.setString(7, evenement.getLocation());
            ps.setInt(8, 0);  // If you have a `salle_id` field in your table, replace 0 with the actual ID.

            ps.executeUpdate();
            System.out.println("Evenement modifié avec succès !");
        }
    }

    @Override
    public void supprimer(int idEvenement) throws SQLException {
        String sql = "DELETE FROM evenement WHERE id_evenement=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);
            ps.executeUpdate();
            System.out.println("Evenement supprimé avec succès !");
        }
    }

    @Override
    public List<Evenement> afficher() throws SQLException {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM evenement";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Evenement evenement = new Evenement(
                        rs.getInt("id_evenement"),
                        rs.getString("titre"),
                        rs.getString("description_evenement"),
                        rs.getString("image"),
                        rs.getString("location"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate()
                );
                evenements.add(evenement);
            }
        }
        return evenements;
    }

    public boolean salleExiste(int salleId) {
        String query = "SELECT COUNT(*) FROM salle WHERE id_salle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, salleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getSalleNames() throws SQLException {
        List<String> salleNames = new ArrayList<>();
        String sql = "SELECT nom_salle FROM salle";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                salleNames.add(rs.getString("nom_salle"));
            }
        }
        return salleNames;
    }

    public int getSalleIdByName(String salleName) throws SQLException {
        String query = "SELECT id_salle FROM salle WHERE nom_salle = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, salleName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_salle");
            }
        }
        return -1; // Return -1 if not found
    }

    // Method to get an event by its ID
    public Evenement getEventById(int eventId) throws SQLException {
        String sql = "SELECT * FROM evenement WHERE id_evenement = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Evenement(
                        rs.getInt("id_evenement"),
                        rs.getString("titre"),
                        rs.getString("description_evenement"),
                        rs.getString("image"),
                        rs.getString("location"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getInt("salle_id")
                );
            }
        }
        return null;
    }

    // Method to get the salle name by its ID
    public String getSalleNameById(int salleId) throws SQLException {
        String sql = "SELECT nom FROM salle WHERE id_salle = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, salleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nom_salle");
            }
        }
        return null;
    }



}
