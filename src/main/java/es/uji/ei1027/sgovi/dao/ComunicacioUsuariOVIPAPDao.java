package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ComunicacioUsuariOVIPAPDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addComunicacio(ComunicacioUsuariOVIPAP comunicacio) {
        jdbcTemplate.update(
                "INSERT INTO comunicaciousuariovipap (idcomunicacio, destinatari, assumpte, datahora, emissor, missatge, tipuscomunicacio) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                comunicacio.getIdComunicacio(),
                comunicacio.getDestinatari(),
                comunicacio.getAssumpte(),
                Timestamp.valueOf(comunicacio.getDataHora()),
                comunicacio.getEmissor(),
                comunicacio.getMissatge(),
                comunicacio.getTipusComunicacio()
        );
    }

    public void deleteComunicacio(int idComunicacio) {
        jdbcTemplate.update(
                "DELETE FROM comunicaciousuariovipap WHERE idcomunicacio = ?",
                idComunicacio
        );
    }

    public void updateComunicacio(ComunicacioUsuariOVIPAP comunicacio) {
        jdbcTemplate.update(
                "UPDATE comunicaciousuariovipap SET destinatari = ?, assumpte = ?, datahora = ?, emissor = ?, missatge = ?, tipuscomunicacio = ? " +
                        "WHERE idcomunicacio = ?",
                comunicacio.getDestinatari(),
                comunicacio.getAssumpte(),
                Timestamp.valueOf(comunicacio.getDataHora()),
                comunicacio.getEmissor(),
                comunicacio.getMissatge(),
                comunicacio.getTipusComunicacio(),
                comunicacio.getIdComunicacio()
        );
    }

    public ComunicacioUsuariOVIPAP getComunicacio(int idComunicacio) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM comunicaciousuariovipap WHERE idcomunicacio = ?",
                    new ComunicacioUsuariOVIPAPRowMapper(),
                    idComunicacio
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ComunicacioUsuariOVIPAP> getComunicacions() {
        return jdbcTemplate.query(
                "SELECT * FROM comunicaciousuariovipap ORDER BY datahora DESC",
                new ComunicacioUsuariOVIPAPRowMapper()
        );
    }

    public List<ComunicacioUsuariOVIPAP> getComunicacionsByDestinatari(String email) {
        return jdbcTemplate.query(
                "SELECT * FROM comunicaciousuariovipap WHERE destinatari = ? ORDER BY datahora DESC",
                new ComunicacioUsuariOVIPAPRowMapper(),
                email
        );
    }

    // Obté el pròxim ID disponible
    public int getNextId() {
        Integer max = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(idcomunicacio), 0) FROM comunicaciousuariovipap",
                Integer.class
        );
        return (max != null ? max : 0) + 1;
    }
}
