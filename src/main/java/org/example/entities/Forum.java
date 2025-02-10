package org.example.entities;


import java.util.Date;

public class Forum {
    private int idForum;
    private String titreForum;
    private String descriptionForum;
    private Date dateCreation;
    private int userId;

    public Forum() {}

    public Forum(int idForum, String titreForum, String descriptionForum, Date dateCreation, int userId) {
        this.idForum = idForum;
        this.titreForum = titreForum;
        this.descriptionForum = descriptionForum;
        this.dateCreation = dateCreation;
        this.userId = userId;
    }
    public Forum(int idForum, String titreForum, String descriptionForum, Date dateCreation) {
        this.idForum = idForum;
        this.titreForum = titreForum;
        this.descriptionForum = descriptionForum;
        this.dateCreation = dateCreation;

    }

    public int getIdForum() {
        return idForum;
    }

    public void setIdForum(int idForum) {
        this.idForum = idForum;
    }

    public String getTitreForum() {
        return titreForum;
    }

    public void setTitreForum(String titreForum) {
        this.titreForum = titreForum;
    }

    public String getDescriptionForum() {
        return descriptionForum;
    }

    public void setDescriptionForum(String descriptionForum) {
        this.descriptionForum = descriptionForum;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "idForum=" + idForum +
                ", titreForum='" + titreForum + '\'' +
                ", descriptionForum='" + descriptionForum + '\'' +
                ", dateCreation=" + dateCreation +
                ", userId=" + userId +
                '}';
    }
}
