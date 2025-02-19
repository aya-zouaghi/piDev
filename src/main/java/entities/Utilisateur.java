package entities;

public class Utilisateur {

private int id_user;
private String nom,prenom, password,statut_compte,role,email;

    public Utilisateur() {
    }
    public Utilisateur(int id_user, String nom) {
        this.id_user = id_user;
        this.nom = nom;}
    public Utilisateur(int id_user, String nom, String prenom, String password, String statut_compte, String role, String email) {
        this.id_user = id_user;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.statut_compte = statut_compte;
        this.role = role;
        this.email = email;
    }
    public int getId_user() {
        return id_user;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getPassword() {
        return password;
    }

    public String getStatut_compte() {
        return statut_compte;
    }
    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setStatut_compte(String statut_compte) {
        this.statut_compte = statut_compte;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String toString() {
        return "Utilisateur{" +
                "id_user=" + id_user +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", password='" + password + '\'' +
                ", statut_compte='" + statut_compte + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
