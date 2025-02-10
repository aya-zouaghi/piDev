package org.example.services;

import org.example.entities.Forum;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumService implements IService<Forum>{

    Connection connection;

    public ForumService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Forum forum) throws SQLException {
        String req = "INSERT INTO forum (titre_forum, description_forum, date_creation, user_id) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, forum.getTitreForum());
        preparedStatement.setString(2, forum.getDescriptionForum());
        preparedStatement.setDate(3, new java.sql.Date(forum.getDateCreation().getTime()));
        preparedStatement.setInt(4, forum.getUserId());
        preparedStatement.executeUpdate();
        System.out.println("Forum ajouté avec succès !");
    }

    @Override
    public void modifier(Forum forum) throws SQLException {
        String req = "UPDATE forum SET titre_forum=?, description_forum=?, date_creation=?, user_id=? WHERE id_forum=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, forum.getTitreForum());
        preparedStatement.setString(2, forum.getDescriptionForum());
        preparedStatement.setDate(3, new java.sql.Date(forum.getDateCreation().getTime()));
        preparedStatement.setInt(4, forum.getUserId());
        preparedStatement.setInt(5, forum.getIdForum());
        preparedStatement.executeUpdate();
        System.out.println("Forum modifié avec succès !");

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM forum WHERE id_forum=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Forum supprimé avec succès !");

    }

    @Override
    public List<Forum> afficher() throws SQLException {
        List<Forum> forums = new ArrayList<>();
        String req = "SELECT * FROM forum";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);
        while (rs.next()) {
            Forum forum = new Forum(
                    rs.getInt("id_forum"),
                    rs.getString("titre_forum"),
                    rs.getString("description_forum"),
                    rs.getDate("date_creation"),
                    rs.getInt("user_id")
            );
            forums.add(forum);
        }
        return forums;
    }
}
