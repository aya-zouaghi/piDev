package SERVICE;

import org.example.entites.DemandeOffre;
import java.sql.SQLException;
import java.util.List;

public interface Idemande {
    void ajouter(DemandeOffre demande) throws SQLException;
    void modifier(DemandeOffre demande) throws SQLException;
    void supprimer(int idDemande) throws SQLException;
    List<DemandeOffre> afficher() throws SQLException;
}
