package org.example.entities;


import java.util.Date;

public class Commentaire {
    private int idCommentaire;
    private String contenu;
    private Date datePublication;
    private int userId;
    private int forumId;

    // Default constructor
    public Commentaire() {}

    // Constructor with all fields
    public Commentaire(int idCommentaire, String contenu, Date datePublication, int userId, int forumId) {
        this.idCommentaire = idCommentaire;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.userId = userId;
        this.forumId = forumId;
    }

    // Constructor without id (useful for creating new comments)
    public Commentaire(String contenu, Date datePublication, int userId, int forumId) {
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.userId = userId;
        this.forumId = forumId;
    }

    public int getIdCommentaire() {
        return idCommentaire;
    }

    public void setIdCommentaire(int idCommentaire) {
        this.idCommentaire = idCommentaire;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "idCommentaire=" + idCommentaire +
                ", contenu='" + contenu + '\'' +
                ", datePublication=" + datePublication +
                ", userId=" + userId +
                ", forumId=" + forumId +
                '}';
    }
}
