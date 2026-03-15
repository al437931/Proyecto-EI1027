package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.AssistentPersonal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AssistentPersonalDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAssistentPersonal(AssistentPersonal assistent) {
        jdbcTemplate.update(
                "INSERT INTO assistentpersonal (idassistent, nom, cognoms, email, telefon, formacio, experiencia, disponibilitat, estatacceptat) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                assistent.getIdAssistent(),
                assistent.getNom(),
                assistent.getCognoms(),
                assistent.getEmail(),
                assistent.getTelefon(),
                assistent.getFormacio(),
                assistent.getExperiencia(),
                assistent.getDisponibilitat(),
                assistent.isEstatAcceptat()
        );
    }

    public void deleteAssistentPersonal(int idAssistent) {
        jdbcTemplate.update(
                "DELETE FROM assistentpersonal WHERE idassistent = ?",
                idAssistent
        );
    }

    public void updateAssistentPersonal(AssistentPersonal assistent) {
        jdbcTemplate.update(
                "UPDATE assistentpersonal SET nom = ?, cognoms = ?, email = ?, telefon = ?, formacio = ?, experiencia = ?, disponibilitat = ?, estatacceptat = ? " +
                        "WHERE idassistent = ?",
                assistent.getNom(),
                assistent.getCognoms(),
                assistent.getEmail(),
                assistent.getTelefon(),
                assistent.getFormacio(),
                assistent.getExperiencia(),
                assistent.getDisponibilitat(),
                assistent.isEstatAcceptat(),
                assistent.getIdAssistent()
        );
    }

    public AssistentPersonal getAssistentPersonal(int idAssistent) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM assistentpersonal WHERE idassistent = ?",
                    new AssistentPersonalRowMapper(),
                    idAssistent
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AssistentPersonal> getAssistentsPersonals() {
        return jdbcTemplate.query(
                "SELECT * FROM assistentpersonal ORDER BY idassistent",
                new AssistentPersonalRowMapper()
        );
    }
}
