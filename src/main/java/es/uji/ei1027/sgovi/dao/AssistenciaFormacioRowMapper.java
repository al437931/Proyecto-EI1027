package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.AssistenciaFormacio;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class AssistenciaFormacioRowMapper implements RowMapper<AssistenciaFormacio> {
    public AssistenciaFormacio mapRow(ResultSet rs, int numRow) throws SQLException {
        AssistenciaFormacio assistenciaFormacio = new AssistenciaFormacio();
        assistenciaFormacio.setIdAssistencia(rs.getInt("idassistencia"));
        assistenciaFormacio.setIdActivitat(rs.getInt("idactivitat"));
        assistenciaFormacio.setIdAssistent(rs.getInt("idassistent"));
        assistenciaFormacio.setAssistent(rs.getBoolean("assistent"));
        assistenciaFormacio.setCertificatEmes(rs.getBoolean("certificatemes"));
        return assistenciaFormacio;
    }
}
