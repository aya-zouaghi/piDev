package com.eventcraft.service;


import java.sql.SQLException;
import java.util.List;

public interface NihedService<T> {
    void ajouter(T t) throws SQLException;
    void modifier(T t) throws SQLException;
    void supprimer(int id) throws SQLException;
    List<T> afficher() throws SQLException;
    T getById(int id) throws SQLException; // Retourne un objet de type T
}
