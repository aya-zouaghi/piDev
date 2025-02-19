package com.eventcraft.model;

import java.sql.Date;
import java.time.LocalDate;

public class Offre {
    private int idOffre;
    private String titreOffre;
    private String descriptionOffre;
    private String typeOffre;
    private float montant;
    private Date dateExp;  // Keep using java.sql.Date
    private int evenement;
    private Integer user;

    // Constructor using java.util.Date
    public Offre(int idOffre, String titreOffre, String descriptionOffre, String typeOffre, float montant, java.util.Date dateExp, int evenement, Integer user) {
        this.idOffre = idOffre;
        this.titreOffre = titreOffre;
        this.descriptionOffre = descriptionOffre;
        this.typeOffre = typeOffre;
        this.montant = montant;
        // Convert java.util.Date to java.sql.Date
        this.dateExp = new Date(dateExp.getTime());
        this.evenement = evenement;
        this.user = user;
    }

    // Constructor without idOffre, for when creating new offers
    public Offre(String titreOffre, String descriptionOffre, String typeOffre, float montant, java.util.Date dateExp, int evenement, Integer user) {
        this.titreOffre = titreOffre;
        this.descriptionOffre = descriptionOffre;
        this.typeOffre = typeOffre;
        this.montant = montant;
        this.dateExp = new Date(dateExp.getTime());
        this.evenement = evenement;
        this.user = user;
    }

    // Getters and Setters
    public int getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public String getTitreOffre() {
        return titreOffre;
    }

    public void setTitreOffre(String titreOffre) {
        this.titreOffre = titreOffre;
    }

    public String getDescriptionOffre() {
        return descriptionOffre;
    }

    public void setDescriptionOffre(String descriptionOffre) {
        this.descriptionOffre = descriptionOffre;
    }

    public String getTypeOffre() {
        return typeOffre;
    }

    public void setTypeOffre(String typeOffre) {
        this.typeOffre = typeOffre;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public Date getDateExp() {
        return dateExp;
    }

    public void setDateExp(Date dateExp) {
        this.dateExp = dateExp;
    }

    // Method to return dateExp as LocalDate
    public LocalDate getDateExpAsLocalDate() {
        return dateExp != null ? dateExp.toLocalDate() : null;
    }

    public int getEvenement() {
        return evenement;
    }

    public void setEvenement(int evenement) {
        this.evenement = evenement;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    // Corrected method for description
    public String getDescription() {
        return descriptionOffre;  // Return the descriptionOffre field
    }

    // Corrected method for date
    public String getDate() {
        return dateExp != null ? dateExp.toString() : "No Date";  // Return the dateExp field as a string
    }
}
