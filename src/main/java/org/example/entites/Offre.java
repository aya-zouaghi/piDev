package org.example.entites;


import java.util.Date;

public class Offre {
    private int idOffre;
    private String titreOffre;
    private String descriptionOffre;
    private String typeOffre;
    private float montant;
    private Date dateExp;
    private int evenement;
    private Integer user;

    public Offre(int idOffre, String titreOffre, String descriptionOffre, String typeOffre, float montant, Date dateExp, int evenement, Integer user) {
        this.idOffre = idOffre;
        this.titreOffre = titreOffre;
        this.descriptionOffre = descriptionOffre;
        this.typeOffre = typeOffre;
        this.montant = montant;
        this.dateExp = dateExp;
        this.evenement = evenement;
        this.user = user;
    }

    public Offre(String titreOffre, String descriptionOffre, String typeOffre, float montant, Date dateExp, int evenement, Integer user) {
        this.titreOffre = titreOffre;
        this.descriptionOffre = descriptionOffre;
        this.typeOffre = typeOffre;
        this.montant = montant;
        this.dateExp = dateExp;
        this.evenement = evenement;
        this.user = user;
    }

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
}
