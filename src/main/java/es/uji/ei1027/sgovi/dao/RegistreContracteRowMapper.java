package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.RegistreContracte;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class RegistreContracteRowMapper implements RowMapper<RegistreContracte> {
    public RegistreContracte mapRow(ResultSet rs, int numRow) throws SQLException {
        RegistreContracte registreContracte = new RegistreContracte();
        registreContracte.setIdContracte(rs.getInt("idcontracte"));
        registreContracte.setIdAssistent(rs.getInt("idassistent"));
        registreContracte.setIdRequest(rs.getInt("idrequest"));
        registreContracte.setDataInici(rs.getDate("datainici").toLocalDate());

        Date dataFi = rs.getDate("datafi");
        registreContracte.setDataFi(dataFi != null ? dataFi.toLocalDate() : null);

        registreContracte.setPdfContracte(rs.getString("pdfcontracte"));
        return registreContracte;
    }
}
