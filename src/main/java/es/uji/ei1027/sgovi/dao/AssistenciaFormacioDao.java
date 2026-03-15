package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.AssistenciaFormacio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AssistenciaFormacioDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAssistenciaFormacio(AssistenciaFormacio assistencia) {
        jdbcTemplate.update(
                "INSERT INTO assistenciaformacio (idassistencia, idactivitat, idassistent, assistent, certificatemes) " +
                        "VALUES (?, ?, ?, ?, ?)",
                assistencia.getIdAssistencia(),
                assistencia.getIdActivitat(),
                assistencia.getIdAssistent(),
                assistencia.isAssistent(),
                assistencia.isCertificatEmes()
        );
    }

    public void deleteAssistenciaFormacio(int idAssistencia) {
        jdbcTemplate.update(
                "DELETE FROM assistenciaformacio WHERE idassistencia = ?",
                idAssistencia
        );
    }

    public void updateAssistenciaFormacio(AssistenciaFormacio assistencia) {
        jdbcTemplate.update(
                "UPDATE assistenciaformacio SET idactivitat = ?, idassistent = ?, assistent = ?, certificatemes = ? " +
                        "WHERE idassistencia = ?",
                assistencia.getIdActivitat(),
                assistencia.getIdAssistent(),
                assistencia.isAssistent(),
                assistencia.isCertificatEmes(),
                assistencia.getIdAssistencia()
        );
    }

    public AssistenciaFormacio getAssistenciaFormacio(int idAssistencia) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM assistenciaformacio WHERE idassistencia = ?",
                    new AssistenciaFormacioRowMapper(),
                    idAssistencia
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AssistenciaFormacio> getAssistenciesFormacio() {
        return jdbcTemplate.query(
                "SELECT * FROM assistenciaformacio ORDER BY idassistencia",
                new AssistenciaFormacioRowMapper()
        );
    }
}
