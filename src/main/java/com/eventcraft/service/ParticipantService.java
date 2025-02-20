package com.eventcraft.service;

import com.eventcraft.DatabaseConnection;
import com.eventcraft.model.Participation;
import com.eventcraft.util.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipantService implements IService<Participation> {
    Connection connection;
    public ParticipantService() {
        connection = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(Participation participation) throws SQLException {
        int loggedInUserId = SessionManager.getUserId();

        if (loggedInUserId == -1) {
            System.err.println("Erreur: Aucun utilisateur connecté !");
            return;
        }

        // Check if the event exists
        String checkEventQuery = "SELECT COUNT(*) FROM evenement WHERE id_evenement = ?";
        PreparedStatement checkEventStmt = connection.prepareStatement(checkEventQuery);
        checkEventStmt.setInt(1, participation.getEvenementId());
        ResultSet rs = checkEventStmt.executeQuery();

        if (rs.next() && rs.getInt(1) == 0) {
            System.err.println("Erreur: L'événement sélectionné n'existe pas !");
            return;
        }

        // Insert participation
        String sql = "INSERT INTO participation (user_id, evenement_id, date_inscription, statut) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, loggedInUserId); // Ensure the participant is the logged-in user
        ps.setInt(2, participation.getEvenementId());
        ps.setDate(3, new java.sql.Date(participation.getDateInscription().getTime()));
        ps.setString(4, participation.getStatut());
        ps.executeUpdate();

        System.out.println("✅ Participant ajouté avec succès !");
    }


    @Override
    public void modifier(Participation participation) throws SQLException {
        String sql = "UPDATE participation SET user_id=?, evenement_id=?, date_inscription=?, statut=? WHERE id_participation=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, participation.getUserId());
        ps.setInt(2, participation.getEvenementId());
        ps.setDate(3, new java.sql.Date(participation.getDateInscription().getTime()));
        ps.setString(4, participation.getStatut());
        ps.setInt(5, participation.getIdParticipation());
        ps.executeUpdate();
        System.out.println("Forum modifié avec succès !");
    }

    @Override
    public void supprimer(int idParticipation) throws SQLException {
        String sql = "DELETE FROM participation WHERE id_participation=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idParticipation);
        ps.executeUpdate();
        System.out.println("Forum supprimé avec succès !");
    }

    @Override
    public List<Participation> afficher() throws SQLException {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT * FROM participation";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            participations.add(new Participation(
                    rs.getInt("id_participation"),
                    rs.getInt("user_id"),
                    rs.getInt("evenement_id"),
                    rs.getDate("date_inscription"),
                    rs.getString("statut")
            ));
        }
        return participations;
    }
}
