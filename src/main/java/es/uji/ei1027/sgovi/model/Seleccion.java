package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class Seleccion {
    private int idSeleccion;
    private Integer idRequest;
    private Integer idAssistent;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataProposta;
    private String estat;
    
    // Transient fields for UI display
    private String nomUsuariComplet;
    private String nomAssistentComplet;

    public Seleccion() {

    }


    public int getIdSeleccion() {
        return idSeleccion;
    }

    public void setIdSeleccion(int idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(Integer idRequest) {
        this.idRequest = idRequest;
    }

    public int getIdAssistent() {
        return idAssistent;
    }

    public void setIdAssistent(Integer idAssistent) {
        this.idAssistent = idAssistent;
    }

    public LocalDate getDataProposta() {
        return dataProposta;
    }

    public void setDataProposta(LocalDate dataProposta) {
        this.dataProposta = dataProposta;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }

    public String getNomUsuariComplet() {
        return nomUsuariComplet;
    }

    public void setNomUsuariComplet(String nomUsuariComplet) {
        this.nomUsuariComplet = nomUsuariComplet;
    }

    public String getNomAssistentComplet() {
        return nomAssistentComplet;
    }

    public void setNomAssistentComplet(String nomAssistentComplet) {
        this.nomAssistentComplet = nomAssistentComplet;
    }

    @Override
    public String toString() {
        return "Seleccion{" +
                "idSeleccion=" + idSeleccion +
                ", idRequest=" + idRequest +
                ", idAssistent=" + idAssistent +
                ", dataProposta=" + dataProposta +
                ", estat='" + estat + '\'' +
                '}';
    }
}
