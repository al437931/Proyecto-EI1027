package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class ComunicacioUsuariOVIPAP {
    private int idComunicacio;
    private String destinatari; // email del destinatari
    private String assumpte;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHora;
    private String emissor; // "tecnic"
    private String missatge;
    private String tipusComunicacio; // "acceptacio_usuari", "rebuig_usuari", "acceptacio_assistent", "rebuig_assistent", "acceptacio_solicitud", "rebuig_solicitud", "general"

    public ComunicacioUsuariOVIPAP() {
    }

    public int getIdComunicacio() {
        return idComunicacio;
    }

    public void setIdComunicacio(int idComunicacio) {
        this.idComunicacio = idComunicacio;
    }

    public String getDestinatari() {
        return destinatari;
    }

    public void setDestinatari(String destinatari) {
        this.destinatari = destinatari;
    }

    public String getAssumpte() {
        return assumpte;
    }

    public void setAssumpte(String assumpte) {
        this.assumpte = assumpte;
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

    public String getTipusComunicacio() {
        return tipusComunicacio;
    }

    public void setTipusComunicacio(String tipusComunicacio) {
        this.tipusComunicacio = tipusComunicacio;
    }

    @Override
    public String toString() {
        return "ComunicacioUsuariOVIPAP{" +
                "idComunicacio=" + idComunicacio +
                ", destinatari='" + destinatari + '\'' +
                ", assumpte='" + assumpte + '\'' +
                ", dataHora=" + dataHora +
                ", emissor='" + emissor + '\'' +
                ", missatge='" + missatge + '\'' +
                ", tipusComunicacio='" + tipusComunicacio + '\'' +
                '}';
    }
}
