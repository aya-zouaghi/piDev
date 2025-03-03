package com.eventcraft.dao;

import com.eventcraft.model.Reclamation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReclamationDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/event_craft";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public boolean saveReclamation(Reclamation reclamation) {
        String insertSQL = "INSERT INTO reclamation (titre, description, date, statut, type, id_user) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
}