package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ActivitatFormacio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

@Repository
public class ActivitatFormacioDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addActivitatFormacio(ActivitatFormacio activitat) {
        jdbcTemplate.update(
                "INSERT INTO activitatformacio (idactivitat, idformador, titol, descripcio, data, tipus, aforament) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                activitat.getIdActivitat(),
                activitat.getIdFormador(),
                activitat.getTitol(),
                activitat.getDescripcio(),
                Date.valueOf(activitat.getData()),
                activitat.getTipus(),
                activitat.getAforament()
        );
    }

    public void deleteActivitatFormacio(int idActivitat) {
        jdbcTemplate.update(
                "DELETE FROM activitatformacio WHERE idactivitat = ?",
                idActivitat
        );
    }

    public void updateActivitatFormacio(ActivitatFormacio activitat) {
        jdbcTemplate.update(
                "UPDATE activitatformacio SET idformador = ?, titol = ?, descripcio = ?, data = ?, tipus = ?, aforament = ? " +
                        "WHERE idactivitat = ?",
                activitat.getIdFormador(),
                activitat.getTitol(),
                activitat.getDescripcio(),
                Date.valueOf(activitat.getData()),
                activitat.getTipus(),
                activitat.getAforament(),
                activitat.getIdActivitat()
        );
    }

    public ActivitatFormacio getActivitatFormacio(int idActivitat) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM activitatformacio WHERE idactivitat = ?",
                    new ActivitatFormacioRowMapper(),
                    idActivitat
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ActivitatFormacio> getActivitatsFormacio() {
        return jdbcTemplate.query(
                "SELECT * FROM activitatformacio ORDER BY idactivitat",
                new ActivitatFormacioRowMapper()
        );
    }
}
