package es.uji.ei1027.sgovi.model;

public class AssistenciaFormacio {
    private Integer idAssistencia;
    private int idActivitat;
    private Integer idUsuari; // ID de l'usuari OVI inscrit
    private Boolean assisteix; // Ha assistit?
    private Boolean certificatEmes;

    public AssistenciaFormacio() {
    }

    public Integer getIdAssistencia() {
        return idAssistencia;
    }

    public void setIdAssistencia(Integer idAssistencia) {
        this.idAssistencia = idAssistencia;
    }

    public int getIdActivitat() {
        return idActivitat;
    }

    public void setIdActivitat(int idActivitat) {
        this.idActivitat = idActivitat;
    }

    public Integer getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(Integer idUsuari) {
        this.idUsuari = idUsuari;
    }

    public Boolean isAssisteix() {
        return assisteix;
    }

    public void setAssisteix(Boolean assisteix) {
        this.assisteix = assisteix;
    }

    public Boolean isCertificatEmes() {
        return certificatEmes;
    }

    public void setCertificatEmes(Boolean certificatEmes) {
        this.certificatEmes = certificatEmes;
    }

    @Override
    public String toString() {
        return "AssistenciaFormacio{" +
                "idAssistencia=" + idAssistencia +
                ", idActivitat=" + idActivitat +
                ", idUsuari=" + idUsuari +
                ", assisteix=" + assisteix +
                ", certificatEmes=" + certificatEmes +
                '}';
    }
}