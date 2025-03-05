package com.eventcraft.model;

public class Salle {
    private Integer idSalle;
    private String nomSalle;
    private int capacite;
    private String equipement;
    private String imageSalle;
    private String locationSalle;
    private int userId; // Ajout du champ user_id
    private String qualite; // Ajout du champ qualité
    private double prix; // Ajout du champ prix

    // Constructeur avec paramètres (sans disponibilite)
    public Salle(int idSalle, String nomSalle, int capacite, String equipement, String imageSalle,
                 String locationSalle, int userId, String qualite, double prix) {
        this.idSalle = idSalle;
        this.nomSalle = nomSalle;
        this.capacite = capacite;
        this.equipement = equipement;
        this.imageSalle = imageSalle;
        this.locationSalle = locationSalle;
        this.userId = userId;
        this.qualite = qualite;
        this.prix = prix;
    }

    // Constructeur sans ID (pour l'ajout, sans disponibilite)
    public Salle(String nomSalle, int capacite, String equipement, String imageSalle,
                 String locationSalle, int userId, String qualite, double prix) {
        this.nomSalle = nomSalle;
        this.capacite = capacite;
        this.equipement = equipement;
        this.imageSalle = imageSalle;
        this.locationSalle = locationSalle;
        this.userId = userId;
        this.qualite = qualite;
        this.prix = prix;
    }

    // Getters
    public int getIdSalle() {
        return idSalle;
    }

    public String getNomSalle() {
        return nomSalle;
    }

    public int getCapacite() {
        return capacite;
    }

    public String getEquipement() {
        return equipement;
    }

    public String getImageSalle() {
        return imageSalle;
    }

    public String getLocationSalle() {
        return locationSalle;
    }

    public int getUserId() {
        return userId;
    }

    public String getQualite() {
        return qualite;
    }

    public double getPrix() {
        return prix;
    }

    // Setters
    public void setIdSalle(int idSalle) {
        this.idSalle = idSalle;
    }

    public void setNomSalle(String nomSalle) {
        this.nomSalle = nomSalle;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public void setEquipement(String equipement) {
        this.equipement = equipement;
    }

    public void setImageSalle(String imageSalle) {
        this.imageSalle = imageSalle;
    }

    public void setLocationSalle(String locationSalle) {
        this.locationSalle = locationSalle;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQualite(String qualite) {
        this.qualite = qualite;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    // Méthode toString mise à jour (sans disponibilite)
    @Override
    public String toString() {
        return "Salle{" +
                "idSalle=" + idSalle +
                ", nomSalle='" + nomSalle + '\'' +
                ", capacite=" + capacite +
                ", equipement='" + equipement + '\'' +
                ", imageSalle='" + imageSalle + '\'' +
                ", locationSalle='" + locationSalle + '\'' +
                ", userId=" + userId + // Affichage de userId
                ", qualite='" + qualite + '\'' + // Affichage de la qualité
                ", prix=" + prix + // Affichage du prix
                '}';
    }
}
