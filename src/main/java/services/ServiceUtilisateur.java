package services;

import entities.Utilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IService<Utilisateur> {
    Connection connection;

    public ServiceUtilisateur() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Utilisateur utilisateur) throws SQLException {
        String sql = "INSERT INTO `utilisateur`(`nom`, `prenom`, `password`, `statut_compte`, `role`, `email`) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, utilisateur.getNom());
        ps.setString(2, utilisateur.getPrenom());
        ps.setString(3, utilisateur.getPassword());
        ps.setString(4, utilisateur.getStatut_compte());
        ps.setString(5, utilisateur.getRole());
        ps.setString(6, utilisateur.getEmail());
        ps.executeUpdate();
        System.out.println("Utilisateur ajouté !");
    }

    @Override
    public void modifier(Utilisateur utilisateur) throws SQLException {
        String sql = "UPDATE `utilisateur` SET `nom`=?, `prenom`=?, `password`=?, `statut_compte`=?, `role`=?, `email`=? WHERE `id_user`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, utilisateur.getNom());
        ps.setString(2, utilisateur.getPrenom());
        ps.setString(3, utilisateur.getPassword());
        ps.setString(4, utilisateur.getStatut_compte());
        ps.setString(5, utilisateur.getRole());
        ps.setString(6, utilisateur.getEmail());
        ps.setInt(7, utilisateur.getId_user());
        ps.executeUpdate();
        System.out.println("Utilisateur modifié !");
    }

    @Override
    public void supprimer(int id_user) throws SQLException {
        String sql = "DELETE FROM `utilisateur` WHERE `id_user`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id_user);
        ps.executeUpdate();
        System.out.println("Utilisateur supprimé !");
    }

    @Override
    public List<Utilisateur> afficher() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM `utilisateur`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            utilisateurs.add(new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("password"),
                    rs.getString("statut_compte"),
                    rs.getString("role"),
                    rs.getString("email")
            ));
        }
        return utilisateurs;
    }
    public Utilisateur getUtilisateurById(int id) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE id_user = ?";
        Connection connection = MyDatabase.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Utilisateur(
                        resultSet.getInt("id_user"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("password"),
                        resultSet.getString("statut_compte"),
                        resultSet.getString("role"),
                        resultSet.getString("email")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return null;
    }
}