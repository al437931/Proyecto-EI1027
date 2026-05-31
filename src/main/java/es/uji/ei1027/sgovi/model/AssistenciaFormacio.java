package es.uji.ei1027.sgovi.model;

import org.codehaus.groovy.runtime.dgmimpl.arrays.IntegerArrayGetAtMetaMethod;

public class AssistenciaFormacio {
    private Integer idAssistencia;
    private int idActivitat;
    private Integer idAssistent;
    private Boolean assistent;
    private Boolean certificatEmes;

    public AssistenciaFormacio() {

    }


    public int getIdAssistencia() {
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

    public int getIdAssistent() {
        return idAssistent;
    }

    public void setIdAssistent(Integer idAssistent) {
        this.idAssistent = idAssistent;
    }

    public Boolean isAssistent() {
        return assistent;
    }

    public void setAssistent(Boolean assistent) {
        this.assistent = assistent;
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
                ", idAssistent=" + idAssistent +
                ", assistent=" + assistent +
                ", certificatEmes=" + certificatEmes +
                '}';
    }
}