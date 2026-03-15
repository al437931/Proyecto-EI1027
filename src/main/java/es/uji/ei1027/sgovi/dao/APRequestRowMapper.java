package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.APRequest;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class APRequestRowMapper implements RowMapper<APRequest> {
    public APRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        APRequest apRequest = new APRequest();
        apRequest.setIdRequest(rs.getInt("idrequest"));
        apRequest.setIdUsuari(rs.getInt("idusuari"));
        apRequest.setDataCreacio(rs.getDate("datacreacio").toLocalDate());
        apRequest.setEstat(rs.getString("estat"));
        apRequest.setTipusAssistencia(rs.getString("tipusassistencia"));
        apRequest.setDescripcioNecessitats(rs.getString("descripcionecessitats"));
        return apRequest;
    }
}
