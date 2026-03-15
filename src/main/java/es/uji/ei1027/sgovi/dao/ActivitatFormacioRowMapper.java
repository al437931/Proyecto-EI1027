package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ActivitatFormacio;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ActivitatFormacioRowMapper implements RowMapper<ActivitatFormacio> {
    public ActivitatFormacio mapRow(ResultSet rs, int numRow) throws SQLException {
        ActivitatFormacio activitatFormacio = new ActivitatFormacio();
        activitatFormacio.setIdActivitat(rs.getInt("idactivitat"));
        activitatFormacio.setIdFormador(rs.getInt("idformador"));
        activitatFormacio.setTitol(rs.getString("titol"));
        activitatFormacio.setDescripcio(rs.getString("descripcio"));
        activitatFormacio.setData(rs.getDate("data").toLocalDate());
        activitatFormacio.setTipus(rs.getString("tipus"));
        activitatFormacio.setAforament(rs.getInt("aforament"));
        return activitatFormacio;
    }
}
