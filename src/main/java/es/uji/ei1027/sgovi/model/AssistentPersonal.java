package es.uji.ei1027.sgovi.model;

public class AssistentPersonal {
    private int idAssistent;
    private String nom;
    private String cognoms;
    private String email;
    private String telefon;
    private String formacio;
    private String experiencia;
    private String disponibilitat;
    private Boolean estatAcceptat;

    public AssistentPersonal() {

    }


    public int getIdAssistent() {
        return idAssistent;
    }

    public void setIdAssistent(int idAssistent) {
        this.idAssistent = idAssistent;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognoms() {
        return cognoms;
    }

    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getFormacio() {
        return formacio;
    }

    public void setFormacio(String formacio) {
        this.formacio = formacio;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getDisponibilitat() {
        return disponibilitat;
    }

    public void setDisponibilitat(String disponibilitat) {
        this.disponibilitat = disponibilitat;
    }

    public Boolean isEstatAcceptat() {
        return estatAcceptat;
    }

    public void setEstatAcceptat(Boolean estatAcceptat) {
        this.estatAcceptat = estatAcceptat;
    }

    @Override
    public String toString() {
        return "AssistentPersonal{" +
                "idAssistent=" + idAssistent +
                ", nom='" + nom + '\'' +
                ", cognoms='" + cognoms + '\'' +
                ", email='" + email + '\'' +
                ", telefon='" + telefon + '\'' +
                ", formacio='" + formacio + '\'' +
                ", experiencia='" + experiencia + '\'' +
                ", disponibilitat='" + disponibilitat + '\'' +
                ", estatAcceptat=" + estatAcceptat +
                '}';
    }
}
