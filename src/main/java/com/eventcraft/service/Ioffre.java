package org.example.eventcraft.SERVICE;

// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)


import com.eventcraft.model.Offre;
import com.eventcraft.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface Ioffre {
    void ajouter(Offre offre) throws SQLException;
    void modifier(Offre offre) throws SQLException;
    void supprimer(int idOffre) throws SQLException;
    List<Offre> afficher() throws SQLException;
}

