package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class Seleccion {
    private int idSeleccion;
    private int idRequest;
    private int idAssistent;
    private LocalDate dataProposta;
    private String estat;

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

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public int getIdAssistent() {
        return idAssistent;
    }

    public void setIdAssistent(int idAssistent) {
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
