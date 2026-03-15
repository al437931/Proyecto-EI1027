package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Formador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FormadorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addFormador(Formador formador) {
        jdbcTemplate.update(
                "INSERT INTO formador (idformador, nom, cognoms, especialitat, email) " +
                        "VALUES (?, ?, ?, ?, ?)",
                formador.getIdFormador(),
                formador.getNom(),
                formador.getCognoms(),
                formador.getEspecialitat(),
                formador.getEmail()
        );
    }

    public void deleteFormador(int idFormador) {
        jdbcTemplate.update(
                "DELETE FROM formador WHERE idformador = ?",
                idFormador
        );
    }

    public void updateFormador(Formador formador) {
        jdbcTemplate.update(
                "UPDATE formador SET nom = ?, cognoms = ?, especialitat = ?, email = ? " +
                        "WHERE idformador = ?",
                formador.getNom(),
                formador.getCognoms(),
                formador.getEspecialitat(),
                formador.getEmail(),
                formador.getIdFormador()
        );
    }

    public Formador getFormador(int idFormador) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM formador WHERE idformador = ?",
                    new FormadorRowMapper(),
                    idFormador
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Formador> getFormadors() {
        return jdbcTemplate.query(
                "SELECT * FROM formador ORDER BY idformador",
                new FormadorRowMapper()
        );
    }
}
