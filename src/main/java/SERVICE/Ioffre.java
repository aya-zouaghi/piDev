package SERVICE;

// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)


import org.example.entites.Offre;
import java.sql.SQLException;
import java.util.List;

public interface Ioffre {
    void ajouter(Offre offre) throws SQLException;
    void modifier(Offre offre) throws SQLException;
    void supprimer(int idOffre) throws SQLException;
    List<Offre> afficher() throws SQLException;
}

