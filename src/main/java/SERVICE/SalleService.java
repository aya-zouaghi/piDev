package SERVICE;

import org.example.entities.Salle;
import utilis.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleService implements IService<Salle> {
    private Connection conn = DatabaseConnection.getConnection();

    @Override
    public void ajouter(Salle salle) throws SQLException {
        String query = "INSERT INTO salle (nom_salle, capacité, équipement, image_salle, location_salle, user_id, qualite, prix) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, salle.getNomSalle());
            pst.setInt(2, salle.getCapacite());
            pst.setString(3, salle.getEquipement());
            pst.setString(4, salle.getImageSalle());
            pst.setString(5, salle.getLocationSalle());
            pst.setInt(6, salle.getUserId());
            pst.setString(7, salle.getQualite()); // Ajout de la qualité
            pst.setDouble(8, salle.getPrix()); // Ajout du prix

            pst.executeUpdate();
        }
    }

    @Override
    public void modifier(Salle salle) throws SQLException {
        String query = "UPDATE salle SET nom_salle = ?, capacité = ?, équipement = ?, image_salle = ?, location_salle = ?, user_id = ?, qualite = ?, prix = ? WHERE id_salle = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, salle.getNomSalle());
            pst.setInt(2, salle.getCapacite());
            pst.setString(3, salle.getEquipement());
            pst.setString(4, salle.getImageSalle());
            pst.setString(5, salle.getLocationSalle());
            pst.setInt(6, salle.getUserId());
            pst.setString(7, salle.getQualite()); // Ajout de la qualité
            pst.setDouble(8, salle.getPrix()); // Ajout du prix
            pst.setInt(9, salle.getIdSalle());

            pst.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement("DELETE FROM salle WHERE id_salle = ?")) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    @Override
    public List<Salle> afficher() throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle"; // Suppression du filtrage par disponibilité
        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                // Créer un objet Salle sans la disponibilité
                salles.add(new Salle(
                        rs.getInt("id_salle"),
                        rs.getString("nom_salle"),
                        rs.getInt("capacité"),
                        rs.getString("équipement"),
                        rs.getString("image_salle"),
                        rs.getString("location_salle"),
                        rs.getInt("user_id"),
                        rs.getString("qualite"), // Récupérer la qualité
                        rs.getDouble("prix") // Récupérer le prix
                ));
            }
        }
        return salles;
    }

    @Override
    public Salle getById(int id) throws SQLException {
        String query = "SELECT * FROM salle WHERE id_salle = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Salle(
                            rs.getInt("id_salle"),
                            rs.getString("nom_salle"),
                            rs.getInt("capacité"),
                            rs.getString("équipement"),
                            rs.getString("image_salle"),
                            rs.getString("location_salle"),
                            rs.getInt("user_id"),
                            rs.getString("qualite"), // Récupérer la qualité
                            rs.getDouble("prix") // Récupérer le prix
                    );
                }
            }
        }
        return null;
    }

    public Salle getByName(String nomSalle) throws SQLException {
        String query = "SELECT * FROM salle WHERE nom_salle = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, nomSalle);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Salle(
                        resultSet.getInt("id_salle"),
                        resultSet.getString("nom_salle"),
                        resultSet.getInt("capacité"),
                        resultSet.getString("équipement"),
                        resultSet.getString("image_salle"),
                        resultSet.getString("location_salle"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("qualite"), // Récupérer la qualité
                        resultSet.getDouble("prix") // Récupérer le prix
                );
            }
        }
        return null; // Si aucune salle n'est trouvée
    }
}
