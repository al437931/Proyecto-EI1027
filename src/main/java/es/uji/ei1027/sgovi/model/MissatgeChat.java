package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Missatge del mini-chat entre usuari i assistent,
 * vinculat a una Selecció (proposta de match).
 */
public class MissatgeChat {
    private int idMissatge;
    private int idSeleccion;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHora;
    private String emissor; // "usuari" o "assistent"
    private String nomEmisor; // Nom complet per mostrar
    private String missatge;

    public MissatgeChat() {}

    public int getIdMissatge() { return idMissatge; }
    public void setIdMissatge(int idMissatge) { this.idMissatge = idMissatge; }

    public int getIdSeleccion() { return idSeleccion; }
    public void setIdSeleccion(int idSeleccion) { this.idSeleccion = idSeleccion; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getEmissor() { return emissor; }
    public void setEmissor(String emissor) { this.emissor = emissor; }

    public String getNomEmisor() { return nomEmisor; }
    public void setNomEmisor(String nomEmisor) { this.nomEmisor = nomEmisor; }

    public String getMissatge() { return missatge; }
    public void setMissatge(String missatge) { this.missatge = missatge; }

    @Override
    public String toString() {
        return "MissatgeChat{" +
                "idMissatge=" + idMissatge +
                ", idSeleccion=" + idSeleccion +
                ", dataHora=" + dataHora +
                ", emissor='" + emissor + '\'' +
                ", missatge='" + missatge + '\'' +
                '}';
    }
}
