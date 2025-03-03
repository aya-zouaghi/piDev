package services;

import entities.Decoration;
import entities.Utilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ServiceDecoration implements IService<Decoration>{
    Connection connection;

    public ServiceDecoration() {
        connection = MyDatabase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Decoration decoration) throws SQLException {
        String sql = "INSERT INTO `decoration` (`nom_decor`, `type_decor`, `description_decor`, `prix`, `stock`, `imageDeco`) VALUES (?, ?, ?, ?, ?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, decoration.getNom_decor());
        ps.setString(2, decoration.getType_decor());
        ps.setString(3, decoration.getDescription_decor());
        ps.setFloat(4, decoration.getPrix());
        ps.setInt(5, decoration.getStock());
        ps.setString(6, decoration.getImageDeco());


        ps.executeUpdate();
        System.out.println("Décoration ajoutée");
    }

    public void ajouterDeco(Decoration decoration) throws SQLException {
        String sql = "INSERT INTO `decoration` (`nom_decor`, `type_decor`, `description_decor`, `prix`, `stock`,`user_id`, `imageDeco`) VALUES (?, ?, ?, ?, ?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, decoration.getNom_decor());
        ps.setString(2, decoration.getType_decor());
        ps.setString(3, decoration.getDescription_decor());
        ps.setFloat(4, decoration.getPrix());
        ps.setInt(5, decoration.getStock());
        ps.setString(6, decoration.getImageDeco());

        ps.setInt(7, decoration.getUser().getId_user());
        ps.executeUpdate();
        System.out.println("Décoration ajoutée");
    }
    @Override
    public void modifier(Decoration decoration) throws SQLException {
        String req = "UPDATE decoration SET nom_decor=?, type_decor=?, description_decor=?, prix=?, stock=? ,imageDeco=? WHERE id_decor=?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, decoration.getNom_decor());
            pst.setString(2, decoration.getType_decor());
            pst.setString(3, decoration.getDescription_decor());
            pst.setFloat(4, decoration.getPrix());
            pst.setInt(5, decoration.getStock());
            pst.setString(6, decoration.getImageDeco());
            pst.setInt(7, decoration.getId_decor());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("❌ ERREUR : Aucune ligne mise à jour. L'ID est peut-être incorrect.");
            } else {
                System.out.println("✅ Modification réussie !");
            }
        } catch (SQLException e) {
            System.out.println("❌ ERREUR SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void modifier1(Decoration decoration) throws SQLException {
        // Vérifiez que l'utilisateur existe
        String checkUserQuery = "SELECT * FROM utilisateur WHERE id_user = ?";
        PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
        checkUserStmt.setInt(1, decoration.getUser().getId_user());
        ResultSet resultSet = checkUserStmt.executeQuery();

        if (!resultSet.next()) {
            System.out.println("Erreur : L'utilisateur avec l'ID " + decoration.getUser().getId_user() + " n'existe pas.");
            return;
        }

        // Mettre à jour la décoration
        String sql = "UPDATE `decoration` SET `nom_decor`=?, `type_decor`=?, `description_decor`=?, `prix`=?, `stock`=?, `user_id`=? WHERE `id_decor`=?, `imageDeco`=? WHERE `id_decor`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, decoration.getNom_decor());
        ps.setString(2, decoration.getType_decor());
        ps.setString(3, decoration.getDescription_decor());
        ps.setFloat(4, decoration.getPrix());
        ps.setInt(5, decoration.getStock());
        ps.setInt(6, decoration.getUser().getId_user()); // user_id
        ps.setString(7, decoration.getImageDeco());

        ps.setInt(8, decoration.getId_decor()); // id_decor
        ps.executeUpdate();
        System.out.println("Décoration modifiée");
    }

    @Override
    public void supprimer(int id_decor) throws SQLException {
        String sql = "DELETE FROM `decoration` WHERE `id_decor`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id_decor);
        ps.executeUpdate();
        System.out.println("Décoration supprimée");
    }

    @Override
    public List<Decoration> afficher() throws SQLException {
        List<Decoration> decorations = new ArrayList<>();
        String sql = "SELECT * FROM `decoration`";



        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {  // 🔹 Parcourir tous les résultats
                decorations.add(new Decoration(
                        rs.getInt("id_decor"),
                        rs.getString("nom_decor"),
                        rs.getString("type_decor"),
                        rs.getString("description_decor"),
                        rs.getFloat("prix"),
                        rs.getInt("stock"),
                        rs.getString("imageDeco")

                ));
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL dans afficher() : " + e.getMessage());
            throw e;  // 🔹 Relancer l'exception pour la gestion dans l'interface utilisateur
        }

        return decorations;
    }

    public List<Decoration> afficher1() throws SQLException {
        List<Decoration> decorations = new ArrayList<>();
        String sql = "SELECT * FROM `decoration` INNER JOIN `utilisateur` ON decoration.user_id = utilisateur.id_user";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {

            Utilisateur user = new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("password"),
                    rs.getString("statut_compte"),
                    rs.getString("role"),
                    rs.getString("email")
            );


            decorations.add(new Decoration(
                    rs.getInt("id_decor"),
                    rs.getString("nom_decor"),
                    rs.getString("type_decor"),
                    rs.getString("description_decor"),
                    rs.getFloat("prix"),
                    rs.getInt("stock"),
                    rs.getString("imageDeco"),
                    user

            ));
        }
        return decorations;
    }
    public Decoration getDecorationById(int id) throws SQLException {
        String query = "SELECT * FROM decoration WHERE id_decor = ?";
        Connection connection = MyDatabase.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Decoration(
                        resultSet.getInt("id_decor"),
                        resultSet.getString("nom_decor"),
                        resultSet.getString("type_decor"),
                        resultSet.getString("description_decor"),
                        resultSet.getFloat("prix"),
                        resultSet.getInt("stock"),
                        resultSet.getString("imageDeco")

                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return null;
    }
}
