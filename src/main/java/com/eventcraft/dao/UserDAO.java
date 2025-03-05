package com.eventcraft.dao;

import com.eventcraft.model.User;
import com.eventcraft.util.DatabaseConnection;
import java.sql.*;

public class UserDAO {

    // Method to fetch user by email
    public static User getUserByEmail(String email) {
        User user = null;
        String query = "SELECT * FROM utilisateur WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setIdUser(resultSet.getInt("id_user")); // Use setIdUser
                user.setNom(resultSet.getString("nom"));
                user.setPrenom(resultSet.getString("prenom"));
                user.setPassword(resultSet.getString("password"));
                user.setStatutCompte(resultSet.getString("statut_compte"));
                user.setRole(resultSet.getString("role"));
                user.setEmail(resultSet.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean insertUser(User user) {
        String query = "INSERT INTO utilisateur (nom, prenom, password, statut_compte, role, email) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getPassword()); // TODO: Hash this password
            stmt.setString(4, user.getStatutCompte());
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getEmail());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Return true if insert was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUser(User user) {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, password = ?, role = ?, email = ? WHERE id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getPassword()); // TODO: Hash the password before saving
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getEmail());
            stmt.setInt(6, user.getIdUser()); // Use getIdUser

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Return true if update was successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(User user, String newPassword) {
        String query = "UPDATE utilisateur SET password = ? WHERE id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newPassword);  // Hash password if needed
            stmt.setInt(2, user.getIdUser()); // Use getIdUser

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Corrected emailExists method
    public boolean emailExists(String email) {
        // SQL query to check if the email exists
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?"; // Corrected table name

        try (Connection conn = DatabaseConnection.getConnection(); // Correctly using the connection
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count is greater than 0, email exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User getUserById(int userId) {
        User user = null;
        String query = "SELECT * FROM utilisateur WHERE id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setIdUser(resultSet.getInt("id_user"));
                user.setNom(resultSet.getString("nom"));
                user.setPrenom(resultSet.getString("prenom"));
                user.setPassword(resultSet.getString("password"));
                user.setStatutCompte(resultSet.getString("statut_compte"));
                user.setRole(resultSet.getString("role"));
                user.setEmail(resultSet.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

}
