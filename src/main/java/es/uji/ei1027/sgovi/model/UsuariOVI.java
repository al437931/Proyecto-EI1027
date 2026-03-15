package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class UsuariOVI {
    private int idUsuari;
    private String nom;
    private String cognoms;
    private String email;
    private String telefon;
    private String adreca;
    private Boolean consentimentRGPD;
    private LocalDate dataRegistre;
    private String estatCompte;

    public UsuariOVI() {

    }


    public int getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(int idUsuari) {
        this.idUsuari = idUsuari;
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

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    public Boolean getConsentimentRGPD() {
        return consentimentRGPD;
    }

    public void setConsentimentRGPD(Boolean consentimentRGPD) {
        this.consentimentRGPD = consentimentRGPD;
    }

    public LocalDate getDataRegistre() {
        return dataRegistre;
    }

    public void setDataRegistre(LocalDate dataRegistre) {
        this.dataRegistre = dataRegistre;
    }

    public String getEstatCompte() {
        return estatCompte;
    }

    public void setEstatCompte(String estatCompte) {
        this.estatCompte = estatCompte;
    }

    @Override
    public String toString() {
        return "UsuariOVI{" +
                "idUsuari=" + idUsuari +
                ", nom='" + nom + '\'' +
                ", cognoms='" + cognoms + '\'' +
                ", email='" + email + '\'' +
                ", telefon='" + telefon + '\'' +
                ", adreca='" + adreca + '\'' +
                ", consentimentRGPD=" + consentimentRGPD +
                ", dataRegistre=" + dataRegistre +
                ", estatCompte='" + estatCompte + '\'' +
                '}';
    }
}
