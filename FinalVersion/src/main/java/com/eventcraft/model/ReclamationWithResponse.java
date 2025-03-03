package com.eventcraft.model;

public class ReclamationWithResponse {
    private Reclamation reclamation;
    private Reponse reponse;

    // Constructor
    public ReclamationWithResponse(Reclamation reclamation, Reponse reponse) {
        this.reclamation = reclamation;
        this.reponse = reponse;
    }

    // Getters
    public Reclamation getReclamation() {
        return reclamation;
    }

    public Reponse getReponse() {
        return reponse;
    }

    // Optionally, you can add setters if you need to modify these fields after creation
    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    public void setReponse(Reponse reponse) {
        this.reponse = reponse;
    }
}
