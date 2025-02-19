package com.eventcraft.service;

import com.eventcraft.DatabaseConnection;
import com.eventcraft.model.Decoration;
import com.eventcraft.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDecoration implements IService<Decoration> {
    private final Connection connection;

    public ServiceDecoration() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(Decoration decoration) throws SQLException {
        String sql = "INSERT INTO decoration (nom_decor, type_decor, description_decor, prix, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, decoration.getNom_decor());
            ps.setString(2, decoration.getType_decor());
            ps.setString(3, decoration.getDescription_decor());
            ps.setFloat(4, decoration.getPrix());
            ps.setInt(5, decoration.getStock());
            ps.executeUpdate();
            System.out.println("Décoration ajoutée");
        }
    }

    public void ajouterDeco(Decoration decoration) throws SQLException {
        String sql = "INSERT INTO decoration (nom_decor, type_decor, description_decor, prix, stock, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, decoration.getNom_decor());
            ps.setString(2, decoration.getType_decor());
            ps.setString(3, decoration.getDescription_decor());
            ps.setFloat(4, decoration.getPrix());
            ps.setInt(5, decoration.getStock());
            ps.setInt(6, decoration.getUser().getIdUser());
            ps.executeUpdate();
            System.out.println("Décoration ajoutée");
        }
    }

    @Override
    public void modifier(Decoration decoration) throws SQLException {
        String sql = "UPDATE decoration SET nom_decor=?, type_decor=?, description_decor=?, prix=?, stock=? WHERE id_decor=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, decoration.getNom_decor());
            pst.setString(2, decoration.getType_decor());
            pst.setString(3, decoration.getDescription_decor());
            pst.setFloat(4, decoration.getPrix());
            pst.setInt(5, decoration.getStock());
            pst.setInt(6, decoration.getId_decor());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("❌ ERREUR : Aucune ligne mise à jour. L'ID est peut-être incorrect.");
            } else {
                System.out.println("✅ Modification réussie !");
            }
        }
    }

    public void modifier1(Decoration decoration) throws SQLException {
        String checkUserQuery = "SELECT 1 FROM utilisateur WHERE id_user = ?";
        try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery)) {
            checkUserStmt.setInt(1, decoration.getUser().getIdUser());
            try (ResultSet resultSet = checkUserStmt.executeQuery()) {
                if (!resultSet.next()) {
                    System.out.println("Erreur : L'utilisateur avec l'ID " + decoration.getUser().getIdUser() + " n'existe pas.");
                    return;
                }
            }
        }

        String sql = "UPDATE decoration SET nom_decor=?, type_decor=?, description_decor=?, prix=?, stock=?, user_id=? WHERE id_decor=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, decoration.getNom_decor());
            ps.setString(2, decoration.getType_decor());
            ps.setString(3, decoration.getDescription_decor());
            ps.setFloat(4, decoration.getPrix());
            ps.setInt(5, decoration.getStock());
            ps.setInt(6, decoration.getUser().getIdUser());
            ps.setInt(7, decoration.getId_decor());
            ps.executeUpdate();
            System.out.println("Décoration modifiée");
        }
    }

    @Override
    public void supprimer(int id_decor) throws SQLException {
        String sql = "DELETE FROM decoration WHERE id_decor=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id_decor);
            ps.executeUpdate();
            System.out.println("Décoration supprimée");
        }
    }

    @Override
    public List<Decoration> afficher() throws SQLException {
        List<Decoration> decorations = new ArrayList<>();
        String sql = "SELECT * FROM decoration";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                decorations.add(new Decoration(
                        rs.getInt("id_decor"),
                        rs.getString("nom_decor"),
                        rs.getString("type_decor"),
                        rs.getString("description_decor"),
                        rs.getFloat("prix"),
                        rs.getInt("stock")
                ));
            }
        }
        return decorations;
    }

    public List<Decoration> afficher1() throws SQLException {
        List<Decoration> decorations = new ArrayList<>();
        String sql = "SELECT * FROM decoration INNER JOIN utilisateur ON decoration.user_id = utilisateur.id_user";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id_user"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("password"),
                        resultSet.getString("statut_compte"),
                        resultSet.getString("role"),
                        resultSet.getString("email")
                );
                decorations.add(new Decoration(
                        resultSet.getInt("id_decor"),
                        resultSet.getString("nom_decor"),
                        resultSet.getString("type_decor"),
                        resultSet.getString("description_decor"),
                        resultSet.getFloat("prix"),
                        resultSet.getInt("stock"),
                        user
                ));
            }
        }
        return decorations;
    }
}
