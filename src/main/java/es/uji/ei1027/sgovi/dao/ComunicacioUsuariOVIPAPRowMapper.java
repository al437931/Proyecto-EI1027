package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ComunicacioUsuariOVIPAPRowMapper implements RowMapper<ComunicacioUsuariOVIPAP> {
    public ComunicacioUsuariOVIPAP mapRow(ResultSet rs, int numRow) throws SQLException {
        ComunicacioUsuariOVIPAP c = new ComunicacioUsuariOVIPAP();
        c.setIdComunicacio(rs.getInt("idcomunicacio"));
        c.setDestinatari(rs.getString("destinatari"));
        c.setAssumpte(rs.getString("assumpte"));
        c.setDataHora(rs.getTimestamp("datahora").toLocalDateTime());
        c.setEmissor(rs.getString("emissor"));
        c.setMissatge(rs.getString("missatge"));
        c.setTipusComunicacio(rs.getString("tipuscomunicacio"));
        return c;
    }
}
