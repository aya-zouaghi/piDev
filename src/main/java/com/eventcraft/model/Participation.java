package com.eventcraft.model;

import java.util.Date;

public class Participation {
    private int idParticipation;
    private int userId;
    private int evenementId;
    private Date dateInscription;
    private String statut;

    public Participation(int idParticipation, int userId, int evenementId, Date dateInscription, String statut) {
        this.idParticipation = idParticipation;
        this.userId = userId;
        this.evenementId = evenementId;
        this.dateInscription = dateInscription;
        this.statut = statut;
    }

    public Participation(int userId, int evenementId, Date dateInscription, String statut) {
        this.userId = userId;
        this.evenementId = evenementId;
        this.dateInscription = dateInscription;
        this.statut = statut;
    }

    public int getIdParticipation() {
        return idParticipation;
    }

    public void setIdParticipation(int idParticipation) {
        this.idParticipation = idParticipation;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "idParticipation=" + idParticipation +
                ", userId=" + userId +
                ", evenementId=" + evenementId +
                ", dateInscription=" + dateInscription +
                ", statut='" + statut + '\'' +
                '}';
    }

}
