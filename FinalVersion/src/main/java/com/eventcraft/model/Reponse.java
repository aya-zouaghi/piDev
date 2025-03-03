package com.eventcraft.model;

public class Reponse {

    private int idReponse;
    private String contenuReponse;
    private int reclamationId; // Foreign key to the reclamation

    // Default constructor
    public Reponse() {
    }

    // Parameterized constructor
    public Reponse(int idReponse, String contenuReponse, int reclamationId) {
        this.idReponse = idReponse;
        this.contenuReponse = contenuReponse;
        this.reclamationId = reclamationId;
    }

    // Getters and Setters

    public int getIdReponse() {
        return idReponse;
    }

    public void setIdReponse(int idReponse) {
        this.idReponse = idReponse;
    }

    public String getContenuReponse() {
        return contenuReponse;
    }

    public void setContenuReponse(String contenuReponse) {
        this.contenuReponse = contenuReponse;
    }

    public int getReclamationId() {
        return reclamationId;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }
}
