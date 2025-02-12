package SERVICE;

import org.example.entites.DemandeOffre;
import utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrudDemande implements Idemande {
    private Connection connection = MyDataBase.getInstance().getConnection();

    @Override
    public void ajouter(DemandeOffre demande) throws SQLException {
        // Retrieve the next available id_demande (this may need to be adjusted for your specific DB setup)
        String sqlGetNextId = "SELECT MAX(id_demande) + 1 AS next_id FROM demande_offre";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sqlGetNextId);

        int nextId = 1; // Default to 1 if no records exist
        if (rs.next()) {
            nextId = rs.getInt("next_id");
        }

        String sql = "INSERT INTO demande_offre (id_demande, user, offre, statut_demande, date_demande) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, nextId);  // Set the manually generated ID
        ps.setInt(2, demande.getUser());
        ps.setInt(3, demande.getOffre());
        ps.setString(4, demande.getStatutDemande());
        ps.setDate(5, new java.sql.Date(demande.getDateDemande().getTime()));
        ps.executeUpdate();
    }


    @Override
    public void modifier(DemandeOffre demande) throws SQLException {
        String sql = "UPDATE demande_offre SET user=?, offre=?, statut_demande=?, date_demande=? WHERE id_demande=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, demande.getUser());
        ps.setInt(2, demande.getOffre());
        ps.setString(3, demande.getStatutDemande());
        ps.setDate(4, new java.sql.Date(demande.getDateDemande().getTime()));
        ps.setInt(5, demande.getIdDemande());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int idDemande) throws SQLException {
        String sql = "DELETE FROM demande_offre WHERE id_demande=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idDemande);
        ps.executeUpdate();
    }

    @Override
    public List<DemandeOffre> afficher() throws SQLException {
        List<DemandeOffre> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande_offre";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            demandes.add(new DemandeOffre(
                    rs.getInt("id_demande"),
                    rs.getInt("user_id"),
                    rs.getInt("offre"),
                    rs.getString("statut_demande"),
                    rs.getDate("date_demande")
            ));
        }
        return demandes;
    }
}
