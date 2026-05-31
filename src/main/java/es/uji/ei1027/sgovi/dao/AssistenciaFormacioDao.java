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
                "INSERT INTO assistenciaformacio (idassistencia, idactivitat, idusuari, assisteix, certificatemes) " +
                        "VALUES (?, ?, ?, ?, ?)",
                assistencia.getIdAssistencia(),
                assistencia.getIdActivitat(),
                assistencia.getIdUsuari(),
                assistencia.isAssisteix(),
                assistencia.isCertificatEmes()
        );
    }

    public void deleteAssistenciaFormacio(Integer idAssistencia) {
        jdbcTemplate.update(
                "DELETE FROM assistenciaformacio WHERE idassistencia = ?",
                idAssistencia
        );
    }

    public void updateAssistenciaFormacio(AssistenciaFormacio assistencia) {
        jdbcTemplate.update(
                "UPDATE assistenciaformacio SET idactivitat = ?, idusuari = ?, assisteix = ?, certificatemes = ? " +
                        "WHERE idassistencia = ?",
                assistencia.getIdActivitat(),
                assistencia.getIdUsuari(),
                assistencia.isAssisteix(),
                assistencia.isCertificatEmes(),
                assistencia.getIdAssistencia()
        );
    }

    public AssistenciaFormacio getAssistenciaFormacio(Integer idAssistencia) {
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

    // Inscripcions d'una activitat concreta
    public List<AssistenciaFormacio> getAssistenciesByActivitat(int idActivitat) {
        return jdbcTemplate.query(
                "SELECT * FROM assistenciaformacio WHERE idactivitat = ?",
                new AssistenciaFormacioRowMapper(),
                idActivitat
        );
    }

    // Inscripcions d'un usuari concret
    public List<AssistenciaFormacio> getAssistenciesByUsuari(int idUsuari) {
        return jdbcTemplate.query(
                "SELECT * FROM assistenciaformacio WHERE idusuari = ?",
                new AssistenciaFormacioRowMapper(),
                idUsuari
        );
    }

    // Comprovar si un usuari ja està inscrit a una activitat
    public boolean existsInscripcio(int idActivitat, int idUsuari) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM assistenciaformacio WHERE idactivitat = ? AND idusuari = ?",
                Integer.class,
                idActivitat, idUsuari
        );
        return count != null && count > 0;
    }

    // Comptar inscrits d'una activitat
    public int countInscrits(int idActivitat) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM assistenciaformacio WHERE idactivitat = ?",
                Integer.class,
                idActivitat
        );
        return count != null ? count : 0;
    }

    // Obté el pròxim ID disponible
    public int getNextId() {
        Integer max = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(idassistencia), 0) FROM assistenciaformacio",
                Integer.class
        );
        return (max != null ? max : 0) + 1;
    }
}
