package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class RegistreContracte {
    private int idContracte;
    private int idAssistent;
    private int idRequest;
    private LocalDate dataInici;
    private LocalDate dataFi;
    private String pdfContracte;

    public RegistreContracte() {

    }


    public int getIdContracte() {
        return idContracte;
    }

    public void setIdContracte(int idContracte) {
        this.idContracte = idContracte;
    }

    public int getIdAssistent() {
        return idAssistent;
    }

    public void setIdAssistent(int idAssistent) {
        this.idAssistent = idAssistent;
    }

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public LocalDate getDataInici() {
        return dataInici;
    }

    public void setDataInici(LocalDate dataInici) {
        this.dataInici = dataInici;
    }

    public LocalDate getDataFi() {
        return dataFi;
    }

    public void setDataFi(LocalDate dataFi) {
        this.dataFi = dataFi;
    }

    public String getPdfContracte() {
        return pdfContracte;
    }

    public void setPdfContracte(String pdfContracte) {
        this.pdfContracte = pdfContracte;
    }

    @Override
    public String toString() {
        return "RegistreContracte{" +
                "idContracte=" + idContracte +
                ", idAssistent=" + idAssistent +
                ", idRequest=" + idRequest +
                ", dataInici=" + dataInici +
                ", dataFi=" + dataFi +
                ", pdfContracte='" + pdfContracte + '\'' +
                '}';
    }
}
