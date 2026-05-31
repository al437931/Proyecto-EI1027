package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.AssistenciaFormacio;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class AssistenciaFormacioRowMapper implements RowMapper<AssistenciaFormacio> {
    public AssistenciaFormacio mapRow(ResultSet rs, int numRow) throws SQLException {
        AssistenciaFormacio a = new AssistenciaFormacio();
        a.setIdAssistencia(rs.getInt("idassistencia"));
        a.setIdActivitat(rs.getInt("idactivitat"));
        a.setIdUsuari(rs.getInt("idusuari"));
        a.setAssisteix(rs.getBoolean("assisteix"));
        a.setCertificatEmes(rs.getBoolean("certificatemes"));
        return a;
    }
}
