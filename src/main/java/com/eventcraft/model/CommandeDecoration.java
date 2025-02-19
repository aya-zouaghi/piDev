package com.eventcraft.model;

import java.time.LocalDate;

public class CommandeDecoration {
    private int id_commande,quantité;

    private LocalDate date_commande;
    private float prix;
    private Evenement evenement_id;
    private Decoration decoration;

    public CommandeDecoration() {
        this.date_commande = LocalDate.now();
    }

    public CommandeDecoration(int id_commande, int quantité, LocalDate date_commande, float prix, Evenement evenement_id, Decoration decoration) {
        this.id_commande = id_commande;
        this.quantité = quantité;
        this.date_commande = date_commande;
        this.prix = prix;
        this.evenement_id = evenement_id;
        this.decoration = decoration;
    }
    public CommandeDecoration(int quantité, LocalDate date_commande, float prix, Evenement evenement_id, Decoration decoration) {
        this.quantité = quantité;
        this.date_commande = (date_commande != null) ? date_commande : LocalDate.now();
        this.prix = prix;
        this.evenement_id = evenement_id;
        this.decoration = decoration;
    }
    public CommandeDecoration(int quantité, LocalDate date_commande, float prix,  Decoration decoration) {
        this.quantité = quantité;
        this.date_commande = (date_commande != null) ? date_commande : LocalDate.now();

        this.prix = prix;
        this.decoration = decoration;
    }

    public int getId_commande() {
        return id_commande;
    }

    public int getQuantité() {
        return quantité;
    }

    public LocalDate getDate_commande() {
        return date_commande;
    }

    public float getPrix() {
        return prix;
    }

    public Evenement getEvenement_id() {
        return evenement_id;
    }
    public Decoration getDecoration() {
        return decoration;
    }

    // Setters
    public void setId_commande(int id_commande) {
        this.id_commande = id_commande;
    }

    public void setQuantité(int quantité) {

        this.quantité = quantité;
        calculerPrixTotal();
    }

    public void setDate_commande(LocalDate date_commande) {
        this.date_commande = (date_commande != null) ? date_commande : LocalDate.now();
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }
    public void setEvenement_id(Evenement evenement_id) {
        this.evenement_id = evenement_id;
    }

    public void setDecoration(Decoration decoration) {
        this.decoration = decoration;
        calculerPrixTotal();
    }
    public void calculerPrixTotal() {
        if (this.decoration != null) {
            this.prix = this.quantité * this.decoration.getPrix();
        }
    }

    @Override
    public String toString() {
        return "CommandeDecoration{" +
                "id_commande=" + id_commande +
                ", quantité=" + quantité +
                ", date_commande=" + date_commande +
                ", prix=" + prix +
                ", evenement_id=" + evenement_id +
                ", decoration=" + decoration +
                '}';
    }

}
