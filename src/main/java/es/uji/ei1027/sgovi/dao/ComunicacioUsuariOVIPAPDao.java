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
                "INSERT INTO comunicaciousuariovipap (idcomunicacio, idseleccion, datahora, emissor, missatge) " +
                        "VALUES (?, ?, ?, ?, ?)",
                comunicacio.getIdComunicacio(),
                comunicacio.getIdSeleccion(),
                Timestamp.valueOf(comunicacio.getDataHora()),
                comunicacio.getEmissor(),
                comunicacio.getMissatge()
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
                "UPDATE comunicaciousuariovipap SET idseleccion = ?, datahora = ?, emissor = ?, missatge = ? " +
                        "WHERE idcomunicacio = ?",
                comunicacio.getIdSeleccion(),
                Timestamp.valueOf(comunicacio.getDataHora()),
                comunicacio.getEmissor(),
                comunicacio.getMissatge(),
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
                "SELECT * FROM comunicaciousuariovipap ORDER BY idcomunicacio",
                new ComunicacioUsuariOVIPAPRowMapper()
        );
    }
}
