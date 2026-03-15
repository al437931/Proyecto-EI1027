package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class ActivitatFormacio {
    private int idActivitat;
    private int idFormador;
    private String titol;
    private String descripcio;
    private LocalDate data;
    private String tipus;
    private int aforament;

    public ActivitatFormacio() {

    }


    public int getIdActivitat() {
        return idActivitat;
    }

    public void setIdActivitat(int idActivitat) {
        this.idActivitat = idActivitat;
    }

    public int getIdFormador() {
        return idFormador;
    }

    public void setIdFormador(int idFormador) {
        this.idFormador = idFormador;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public int getAforament() {
        return aforament;
    }

    public void setAforament(int aforament) {
        this.aforament = aforament;
    }

    @Override
    public String toString() {
        return "ActivitatFormacio{" +
                "idActivitat=" + idActivitat +
                ", idFormador=" + idFormador +
                ", titol='" + titol + '\'' +
                ", descripcio='" + descripcio + '\'' +
                ", data=" + data +
                ", tipus='" + tipus + '\'' +
                ", aforament=" + aforament +
                '}';
    }
}
