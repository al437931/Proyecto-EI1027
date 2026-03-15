package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class APRequest {
    private int idRequest;
    private int idUsuari;
    private LocalDate dataCreacio;
    private String estat;
    private String tipusAssistencia;
    private String descripcioNecessitats;

    public APRequest() {

    }


    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public int getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(int idUsuari) {
        this.idUsuari = idUsuari;
    }

    public LocalDate getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(LocalDate dataCreacio) {
        this.dataCreacio = dataCreacio;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }

    public String getTipusAssistencia() {
        return tipusAssistencia;
    }

    public void setTipusAssistencia(String tipusAssistencia) {
        this.tipusAssistencia = tipusAssistencia;
    }

    public String getDescripcioNecessitats() {
        return descripcioNecessitats;
    }

    public void setDescripcioNecessitats(String descripcioNecessitats) {
        this.descripcioNecessitats = descripcioNecessitats;
    }

    @Override
    public String toString() {
        return "APRequest{" +
                "idRequest=" + idRequest +
                ", idUsuari=" + idUsuari +
                ", dataCreacio=" + dataCreacio +
                ", estat='" + estat + '\'' +
                ", tipusAssistencia='" + tipusAssistencia + '\'' +
                ", descripcioNecessitats='" + descripcioNecessitats + '\'' +
                '}';
    }
}
