package com.eventcraft.model;

public class Decoration {
    private int id_decor,stock;
    private String nom_decor,type_decor,description_decor;
    private float prix;
    private User user_id;

    public Decoration() {
    }
    public Decoration(int id_decor,  String nom_decor, String type_decor, String description_decor, float prix ,int stock) {

        this.id_decor = id_decor;
        this.nom_decor = nom_decor;
        this.type_decor = type_decor;
        this.description_decor = description_decor;
        this.prix = prix;
        this.stock = stock;

    }
    public Decoration(int id_decor,  String nom_decor, String type_decor, String description_decor, float prix ,int stock, User user_id) {

        this.id_decor = id_decor;
        this.nom_decor = nom_decor;
        this.type_decor = type_decor;
        this.description_decor = description_decor;
        this.prix = prix;
        this.stock = stock;
        this.user_id = user_id;
    }

    public Decoration(String nom_decor, String type_decor, String description_decor, float prix , int stock, User user_id ) {

        this.nom_decor = nom_decor;
        this.type_decor = type_decor;
        this.description_decor = description_decor;
        this.prix = prix;
        this.stock = stock;
        this.user_id = user_id;
    }
    public Decoration(String nom_decor, String type_decor, String description_decor, float prix , int stock) {

        this.nom_decor = nom_decor;
        this.type_decor = type_decor;
        this.description_decor = description_decor;
        this.prix = prix;
        this.stock = stock;

    }


    public int getId_decor() {
        return id_decor;
    }

    public void setId_decor(int id_decor) {
        this.id_decor = id_decor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public String getNom_decor() {
        return nom_decor;
    }

    public void setNom_decor(String nom_decor) {
        this.nom_decor = nom_decor;
    }

    public String getType_decor() {
        return type_decor;
    }

    public void setType_decor(String type_decor) {
        this.type_decor = type_decor;
    }

    public String getDescription_decor() {
        return description_decor;
    }

    public void setDescription_decor(String description_decor) {
        this.description_decor = description_decor;
    }
    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }
    public User getUser() {
        return user_id;
    }

    public void setuser_id(User user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Decoration{" +
                "id_decor=" + id_decor +
                ", stock=" + stock +
                ", nom_decor='" + nom_decor + '\'' +
                ", type_decor='" + type_decor + '\'' +
                ", description_decor='" + description_decor + '\'' +
                ", prix=" + prix +
                ", user_id=" + user_id +
                '}';
    }
}
