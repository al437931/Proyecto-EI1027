package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Seleccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

@Repository
public class SeleccionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addSeleccion(Seleccion seleccion) {
        jdbcTemplate.update(
                "INSERT INTO seleccion (idseleccion, idrequest, idassistent, dataproposta, estat) " +
                        "VALUES (?, ?, ?, ?, ?)",
                seleccion.getIdSeleccion(),
                seleccion.getIdRequest(),
                seleccion.getIdAssistent(),
                Date.valueOf(seleccion.getDataProposta()),
                seleccion.getEstat()
        );
    }

    public void deleteSeleccion(int idSeleccion) {
        jdbcTemplate.update(
                "DELETE FROM seleccion WHERE idseleccion = ?",
                idSeleccion
        );
    }

    public void updateSeleccion(Seleccion seleccion) {
        jdbcTemplate.update(
                "UPDATE seleccion SET idrequest = ?, idassistent = ?, dataproposta = ?, estat = ? " +
                        "WHERE idseleccion = ?",
                seleccion.getIdRequest(),
                seleccion.getIdAssistent(),
                Date.valueOf(seleccion.getDataProposta()),
                seleccion.getEstat(),
                seleccion.getIdSeleccion()
        );
    }

    public Seleccion getSeleccion(int idSeleccion) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM seleccion WHERE idseleccion = ?",
                    new SeleccionRowMapper(),
                    idSeleccion
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Seleccion> getSeleccions() {
        return jdbcTemplate.query(
                "SELECT * FROM seleccion ORDER BY idseleccion",
                new SeleccionRowMapper()
        );
    }
}
