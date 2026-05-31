package es.uji.ei1027.sgovi.model;

public class AssistentPersonal {
    private Integer idAssistent;
    private String nom;
    private String cognoms;
    private String email;
    private String telefon;
    private String formacio;
    private String experiencia;
    private String disponibilitat;
    private Boolean estatAcceptat;
    private String password;
    private String tipusAssistent; // "PAP" o "PATI"
    private String motiuRebuig;

    public AssistentPersonal() {

    }


    public int getIdAssistent() {
        return idAssistent;
    }

    public void setIdAssistent(Integer idAssistent) {
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

    public Boolean getEstatAcceptat() {
        return estatAcceptat;
    }

    public void setEstatAcceptat(Boolean estatAcceptat) {
        this.estatAcceptat = estatAcceptat;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipusAssistent() {
        return tipusAssistent;
    }

    public void setTipusAssistent(String tipusAssistent) {
        this.tipusAssistent = tipusAssistent;
    }

    public String getMotiuRebuig() {
        return motiuRebuig;
    }

    public void setMotiuRebuig(String motiuRebuig) {
        this.motiuRebuig = motiuRebuig;
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
                ", tipusAssistent='" + tipusAssistent + '\'' +
                '}';
    }
}