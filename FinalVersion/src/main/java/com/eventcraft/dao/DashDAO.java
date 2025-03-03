package com.eventcraft.dao;
import com.eventcraft.model.Reponse;

import com.eventcraft.model.User;
import com.eventcraft.model.Reclamation;
import com.eventcraft.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashDAO {

    // User-related methods

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("id_user"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setPassword(rs.getString("password"));
                user.setStatutCompte(rs.getString("statut_compte"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUser(User user) {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, password = ?, statut_compte = ?, role = ? WHERE id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getStatutCompte());
            stmt.setString(6, user.getRole());
            stmt.setInt(7, user.getIdUser());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        String query = "DELETE FROM utilisateur WHERE id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Reclamation-related methods

    public boolean saveReclamation(Reclamation reclamation) {
        String insertSQL = "INSERT INTO reclamation (titre, description, date, statut, type, id_user) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, reclamation.getTitre());
            pstmt.setString(2, reclamation.getDescription());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(reclamation.getDate()));
            pstmt.setString(4, reclamation.getStatut());
            pstmt.setString(5, reclamation.getType());
            pstmt.setInt(6, reclamation.getIdUser());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }

    public List<Reclamation> getReclamationsByUserId(int userId) {
        List<Reclamation> reclamations = new ArrayList<>();
        String selectSQL = "SELECT * FROM reclamation WHERE id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(rs.getInt("id_reclamation")); // Assuming 'id_reclamation' is the primary key
                reclamation.setTitre(rs.getString("titre"));
                reclamation.setDescription(rs.getString("description"));
                reclamation.setDate(rs.getTimestamp("date").toLocalDateTime());
                reclamation.setStatut(rs.getString("statut"));
                reclamation.setType(rs.getString("type"));
                reclamation.setIdUser(rs.getInt("id_user"));
                reclamations.add(reclamation);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }

        return reclamations;
    }

    public List<Reclamation> getAllReclamations() {
        List<Reclamation> reclamations = new ArrayList<>();
        String selectSQL = "SELECT * FROM reclamation";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(rs.getInt("id_reclamation")); // Assuming 'id_reclamation' is the primary key
                reclamation.setTitre(rs.getString("titre"));
                reclamation.setDescription(rs.getString("description"));
                reclamation.setDate(rs.getTimestamp("date").toLocalDateTime());
                reclamation.setStatut(rs.getString("statut"));
                reclamation.setType(rs.getString("type"));
                reclamation.setIdUser(rs.getInt("id_user"));
                reclamations.add(reclamation);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }

        return reclamations;
    }

    public void updateReclamation(Reclamation reclamation) {
        String updateSQL = "UPDATE reclamation SET titre = ?, description = ?, date = ?, statut = ?, type = ? WHERE id_reclamation = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, reclamation.getTitre());
            pstmt.setString(2, reclamation.getDescription());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(reclamation.getDate()));
            pstmt.setString(4, reclamation.getStatut());
            pstmt.setString(5, reclamation.getType());
            pstmt.setInt(6, reclamation.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }
    }

    public void deleteReclamation(int reclamationId) {
        String deleteSQL = "DELETE FROM reclamation WHERE id_reclamation = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setInt(1, reclamationId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }
    }
    public boolean saveReponse(int reclamationId, String contenuReponse) {
        String insertSQL = "INSERT INTO reponse (contenu_reponse, reclamation_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, contenuReponse);
            pstmt.setInt(2, reclamationId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }
    public List<Reponse> getReponsesByUserId(int userId) {
        List<Reponse> reponses = new ArrayList<>();
        String selectSQL = "SELECT r.* FROM reponse r JOIN reclamation rc ON r.reclamation_id = rc.id_reclamation WHERE rc.id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reponse reponse = new Reponse();
                reponse.setIdReponse(rs.getInt("id_reponse"));
                reponse.setContenuReponse(rs.getString("contenu_reponse"));
                reponses.add(reponse);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }

        return reponses;
    }
    public Reponse getReponseByReclamationId(int reclamationId) {
        String selectSQL = "SELECT * FROM reponse WHERE reclamation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setInt(1, reclamationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Reponse reponse = new Reponse();
                reponse.setIdReponse(rs.getInt("id_reponse"));
                reponse.setContenuReponse(rs.getString("contenu_reponse"));
                reponse.setReclamationId(reclamationId); // Set the reclamation ID
                return reponse;
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }
        return null; // Return null if no response is found
    }


}
