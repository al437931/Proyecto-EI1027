package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Seleccion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class SeleccionRowMapper implements RowMapper<Seleccion> {
    public Seleccion mapRow(ResultSet rs, int numRow) throws SQLException {
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(rs.getInt("idseleccion"));
        seleccion.setIdRequest(rs.getInt("idrequest"));
        seleccion.setIdAssistent(rs.getInt("idassistent"));
        seleccion.setDataProposta(rs.getDate("dataproposta").toLocalDate());
        seleccion.setEstat(rs.getString("estat"));
        return seleccion;
    }
}
