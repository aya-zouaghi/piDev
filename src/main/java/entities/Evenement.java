package entities;

import java.time.LocalDate;

public class Evenement {
    private int id_evenement;
    private String titre ,description_evenement	,image,location;
    private LocalDate date_debut ,date_fin;
    public Evenement() {
    }


    public Evenement(int id_evenement, String titre, String description_evenement, String image, String location, LocalDate date_debut, LocalDate date_fin) {
        this.id_evenement = id_evenement;
        this.titre = titre;
        this.description_evenement = description_evenement;
        this.image = image;
        this.location = location;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
    }
    public Evenement( String titre, String description_evenement, String image, String location, LocalDate date_debut, LocalDate date_fin) {
        this.titre = titre;
        this.description_evenement = description_evenement;
        this.image = image;
        this.location = location;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
    }
    public int getId_evenement() {
        return id_evenement;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription_evenement() {
        return description_evenement;
    }

    public String getImage() {
        return image;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getDate_debut() {
        return date_debut;
    }

    public LocalDate getDate_fin() {
        return date_fin;
    }

    // Setters
    public void setId_evenement(int id_evenement) {
        this.id_evenement = id_evenement;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription_evenement(String description_evenement) {
        this.description_evenement = description_evenement;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate_debut(LocalDate date_debut) {
        this.date_debut = date_debut;
    }

    public void setDate_fin(LocalDate date_fin) {
        this.date_fin = date_fin;
    }

    // Méthode toString pour afficher l'événement
    @Override
    public String toString() {
        return "Evenement{" +
                "id_evenement=" + id_evenement +
                ", titre='" + titre + '\'' +
                ", description_evenement='" + description_evenement + '\'' +
                ", image='" + image + '\'' +
                ", location='" + location + '\'' +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                '}';
    }
}
