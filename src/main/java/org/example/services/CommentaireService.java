package org.example.services;

import org.example.entities.Commentaire;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CommentaireService implements IService<Commentaire>{

    Connection connection;
    public CommentaireService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Commentaire commentaire) throws SQLException {
        String req = "INSERT INTO commentaire (contenu, date_pub, user_id, forum_id) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(req);
        pst.setString(1, commentaire.getContenu());
        pst.setDate(2, new java.sql.Date(commentaire.getDatePublication().getTime()));
        pst.setInt(3, commentaire.getUserId());
        pst.setInt(4, commentaire.getForumId());
        pst.executeUpdate();
        System.out.println("Commentaire ajouté !");

    }

    @Override
    public void modifier(Commentaire commentaire) throws SQLException {
        String req = "UPDATE commentaire SET contenu=?, date_pub=?, user_id=?, forum_id=? WHERE id_commentaire=?";
        PreparedStatement pst = connection.prepareStatement(req);
        pst.setString(1, commentaire.getContenu());
        pst.setDate(2, new java.sql.Date(commentaire.getDatePublication().getTime()));
        pst.setInt(3, commentaire.getUserId());
        pst.setInt(4, commentaire.getForumId());
        pst.setInt(5, commentaire.getIdCommentaire());
        pst.executeUpdate();
        System.out.println("Commentaire modifié !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM commentaire WHERE id_commentaire=?";
        PreparedStatement pst = connection.prepareStatement(req);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("Commentaire supprimé !");
    }

    @Override
    public List<Commentaire> afficher() throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);
        while (rs.next()) {
            Commentaire commentaire = new Commentaire(
                    rs.getInt("id_commentaire"),
                    rs.getString("contenu"),
                    rs.getDate("date_pub"),
                    rs.getInt("user_id"),
                    rs.getInt("forum_id")
            );
            commentaires.add(commentaire);
        }
        return commentaires;    }
}
