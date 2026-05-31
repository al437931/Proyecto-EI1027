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
                "INSERT INTO assistentpersonal (idassistent, nom, cognoms, email, telefon, formacio, experiencia, disponibilitat, estatacceptat, password, tipusassistent, motiu_rebuig) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                assistent.getIdAssistent(),
                assistent.getNom(),
                assistent.getCognoms(),
                assistent.getEmail(),
                assistent.getTelefon(),
                assistent.getFormacio(),
                assistent.getExperiencia(),
                assistent.getDisponibilitat(),
                assistent.isEstatAcceptat(),
                assistent.getPassword(),
                assistent.getTipusAssistent(),
                assistent.getMotiuRebuig()
        );
    }

    public void deleteAssistentPersonal(Integer idAssistent) {
        jdbcTemplate.update(
                "DELETE FROM assistentpersonal WHERE idassistent = ?",
                idAssistent
        );
    }

    public void updateAssistentPersonal(AssistentPersonal assistent) {
        jdbcTemplate.update(
                "UPDATE assistentpersonal SET nom = ?, cognoms = ?, email = ?, telefon = ?, formacio = ?, experiencia = ?, disponibilitat = ?, estatacceptat = ?, tipusassistent = ?, motiu_rebuig = ?, password = ? " +
                        "WHERE idassistent = ?",
                assistent.getNom(),
                assistent.getCognoms(),
                assistent.getEmail(),
                assistent.getTelefon(),
                assistent.getFormacio(),
                assistent.getExperiencia(),
                assistent.getDisponibilitat(),
                assistent.isEstatAcceptat(),
                assistent.getTipusAssistent(),
                assistent.getMotiuRebuig(),
                assistent.getPassword(),
                assistent.getIdAssistent()
        );
    }

    // Actualitzar la contrasenya d'un assistent
    public void updatePassword(Integer idAssistent, String newPassword) {
        jdbcTemplate.update(
                "UPDATE assistentpersonal SET password = ? WHERE idassistent = ?",
                newPassword, idAssistent
        );
    }

    public AssistentPersonal getAssistentPersonal(Integer idAssistent) {
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

    // Buscar per email (per al login)
    public AssistentPersonal getAssistentByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM assistentpersonal WHERE email = ?",
                    new AssistentPersonalRowMapper(),
                    email.trim()
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

    // Retorna els assistents que han estat acceptats pel tècnic
    public List<AssistentPersonal> getAssistentsAcceptats() {
        return jdbcTemplate.query(
                "SELECT * FROM assistentpersonal WHERE estatacceptat = true ORDER BY nom",
                new AssistentPersonalRowMapper()
        );
    }

    // Retorna els assistents acceptats filtrats per tipus (PAP o PATI)
    public List<AssistentPersonal> getAssistentsAcceptatsByTipus(String tipus) {
        return jdbcTemplate.query(
                "SELECT * FROM assistentpersonal WHERE estatacceptat = true AND tipusassistent = ? ORDER BY nom",
                new AssistentPersonalRowMapper(),
                tipus
        );
    }
}