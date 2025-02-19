package com.eventcraft.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DemandeOffre {
    private int idDemande;
    private int user;
    private int offre;
    private String statutDemande;
    private Date dateDemande;

    // Constructor without idDemande for inserts
    public DemandeOffre(int user, int offre, String statutDemande, Date dateDemande) {
        this.user = user;
        this.offre = offre;
        this.statutDemande = statutDemande;
        this.dateDemande = dateDemande;
    }

    // Constructor with idDemande for fetch or update
    public DemandeOffre(int idDemande, int user, int offre, String statutDemande, Date dateDemande) {
        this.idDemande = idDemande;
        this.user = user;
        this.offre = offre;
        this.statutDemande = statutDemande;
        this.dateDemande = dateDemande;
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getOffre() {
        return offre;
    }

    public void setOffre(int offre) {
        this.offre = offre;
    }

    public String getStatutDemande() {
        return statutDemande;
    }

    public void setStatutDemande(String statutDemande) {
        this.statutDemande = statutDemande;
    }

    public Date getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Date dateDemande) {
        this.dateDemande = dateDemande;
    }

    // Override toString method for better display
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "DemandeOffre{" +
                "idDemande=" + idDemande +
                ", user=" + user +
                ", offre=" + offre +
                ", statutDemande='" + statutDemande + '\'' +
                ", dateDemande=" + sdf.format(dateDemande) +
                '}';
    }
}
