package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;

public class ComunicacioUsuariOVIPAP {
    private int idComunicacio;
    private int idSeleccion;
    private LocalDateTime dataHora;
    private String emissor;
    private String missatge;

    public ComunicacioUsuariOVIPAP() {

    }


    public int getIdComunicacio() {
        return idComunicacio;
    }

    public void setIdComunicacio(int idComunicacio) {
        this.idComunicacio = idComunicacio;
    }

    public int getIdSeleccion() {
        return idSeleccion;
    }

    public void setIdSeleccion(int idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getEmissor() {
        return emissor;
    }

    public void setEmissor(String emissor) {
        this.emissor = emissor;
    }

    public String getMissatge() {
        return missatge;
    }

    public void setMissatge(String missatge) {
        this.missatge = missatge;
    }

    @Override
    public String toString() {
        return "ComunicacioUsuariOVIPAP{" +
                "idComunicacio=" + idComunicacio +
                ", idSeleccion=" + idSeleccion +
                ", dataHora=" + dataHora +
                ", emissor='" + emissor + '\'' +
                ", missatge='" + missatge + '\'' +
                '}';
    }
}
