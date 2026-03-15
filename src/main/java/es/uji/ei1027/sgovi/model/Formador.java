package es.uji.ei1027.sgovi.model;

public class Formador {
    private int idFormador;
    private String nom;
    private String cognoms;
    private String especialitat;
    private String email;

    public Formador() {

    }


    public int getIdFormador() {
        return idFormador;
    }

    public void setIdFormador(int idFormador) {
        this.idFormador = idFormador;
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

    public String getEspecialitat() {
        return especialitat;
    }

    public void setEspecialitat(String especialitat) {
        this.especialitat = especialitat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Formador{" +
                "idFormador=" + idFormador +
                ", nom='" + nom + '\'' +
                ", cognoms='" + cognoms + '\'' +
                ", especialitat='" + especialitat + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
