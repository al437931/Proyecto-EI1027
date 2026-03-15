package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.APRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

@Repository
public class APRequestDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAPRequest(APRequest request) {
        jdbcTemplate.update(
                "INSERT INTO aprequest (idrequest, idusuari, datacreacio, estat, tipusassistencia, descripcioneccessitats) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                request.getIdRequest(),
                request.getIdUsuari(),
                Date.valueOf(request.getDataCreacio()),
                request.getEstat(),
                request.getTipusAssistencia(),
                request.getDescripcioNecessitats()
        );
    }

    public void deleteAPRequest(int idRequest) {
        jdbcTemplate.update(
                "DELETE FROM aprequest WHERE idrequest = ?",
                idRequest
        );
    }

    public void updateAPRequest(APRequest request) {
        jdbcTemplate.update(
                "UPDATE aprequest SET idusuari = ?, datacreacio = ?, estat = ?, tipusassistencia = ?, descripcioneccessitats = ? " +
                        "WHERE idrequest = ?",
                request.getIdUsuari(),
                Date.valueOf(request.getDataCreacio()),
                request.getEstat(),
                request.getTipusAssistencia(),
                request.getDescripcioNecessitats(),
                request.getIdRequest()
        );
    }

    public APRequest getAPRequest(int idRequest) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM aprequest WHERE idrequest = ?",
                    new APRequestRowMapper(),
                    idRequest
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<APRequest> getAPRequests() {
        return jdbcTemplate.query(
                "SELECT * FROM aprequest ORDER BY idrequest",
                new APRequestRowMapper()
        );
    }
}
