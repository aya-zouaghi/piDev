package com.eventcraft.model;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String password;
    private String statutCompte;
    private String role;
    private String email;

    // Default constructor
    public User() {
    }

    // Constructor with all fields
    public User(String nom, String prenom, String password, String statutCompte, String role, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.statutCompte = statutCompte;
        this.role = role;
        this.email = email;
    }

    // Getters and Setters
    public int getIdUser() {
        return id;
    }

    public void setIdUser(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatutCompte() {
        return statutCompte;
    }

    public void setStatutCompte(String statutCompte) {
        this.statutCompte = statutCompte;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
