package com.eventcraft.model;


import java.sql.Date;
import java.time.LocalDate;

public class Reservationsalle {
    private int id_reservation;
    private Integer  salle; // Stocke uniquement l'ID de la salle
    private Integer  user_id; // Stocke uniquement l'ID de l'utilisateur
    private Date date_debut; // Date de début de la réservation
    private Date date_fin; // Date de fin de la réservation

    // Constructeur par défaut
    public Reservationsalle() {}

    // Constructeur avec paramètres
    public Reservationsalle(int id_reservation, int salle, int user_id, Date date_debut, Date date_fin) {
        this.id_reservation = id_reservation;
        this.salle = salle;
        this.user_id = user_id;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
    }

    public Reservationsalle(int idSalle, int userId, LocalDate startDate, LocalDate endDate) {
    }

    // Getters
    public int getId_reservation() {
        return id_reservation;
    }

    public int getSalle() {
        return salle;
    }


    public int getUser_id() {
        return user_id;
    }

    public Date getDate_debut() {
        return date_debut;
    }

    public Date getDate_fin() {
        return date_fin;
    }

    // Setters
    public void setId_reservation(int id_reservation) {
        this.id_reservation = id_reservation;
    }

    public void setSalle(int salle) {
        this.salle = salle;
    }



    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setDate_debut(Date date_debut) {
        this.date_debut = date_debut;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
    }

    @Override
    public String toString() {
        return "ReservationSalle{" +
                "id_reservation=" + id_reservation +
                ", salle=" + salle +
                ", user_id=" + user_id +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                '}';
    }
}
