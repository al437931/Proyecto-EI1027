package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ComunicacioUsuariOVIPAPRowMapper implements RowMapper<ComunicacioUsuariOVIPAP> {
    public ComunicacioUsuariOVIPAP mapRow(ResultSet rs, int numRow) throws SQLException {
        ComunicacioUsuariOVIPAP comunicacioUsuariOVIPAP = new ComunicacioUsuariOVIPAP();
        comunicacioUsuariOVIPAP.setIdComunicacio(rs.getInt("idcomunicacio"));
        comunicacioUsuariOVIPAP.setIdSeleccion(rs.getInt("idseleccion"));
        comunicacioUsuariOVIPAP.setDataHora(rs.getTimestamp("datahora").toLocalDateTime());
        comunicacioUsuariOVIPAP.setEmissor(rs.getString("emissor"));
        comunicacioUsuariOVIPAP.setMissatge(rs.getString("missatge"));
        return comunicacioUsuariOVIPAP;
    }
}
