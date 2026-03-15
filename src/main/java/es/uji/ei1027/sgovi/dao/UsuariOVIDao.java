package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuariOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

@Repository
public class UsuariOVIDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addUsuariOVI(UsuariOVI usuari) {
        jdbcTemplate.update(
                "INSERT INTO usuariovi (idusuari, nom, cognoms, email, telefon, adreca, consentimentrgpd, dataregistre, estatcompte) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                usuari.getIdUsuari(),
                usuari.getNom(),
                usuari.getCognoms(),
                usuari.getEmail(),
                usuari.getTelefon(),
                usuari.getAdreca(),
                usuari.getConsentimentRGPD(),
                Date.valueOf(usuari.getDataRegistre()),
                usuari.getEstatCompte()
        );
    }

    public void deleteUsuariOVI(int idUsuari) {
        jdbcTemplate.update("DELETE FROM usuariovi WHERE idusuari = ?", idUsuari);
    }

    public void updateUsuariOVI(UsuariOVI usuari) {
        jdbcTemplate.update(
                "UPDATE usuariovi SET nom = ?, cognoms = ?, email = ?, telefon = ?, adreca = ?, consentimentrgpd = ?, dataregistre = ?, estatcompte = ? " +
                        "WHERE idusuari = ?",
                usuari.getNom(),
                usuari.getCognoms(),
                usuari.getEmail(),
                usuari.getTelefon(),
                usuari.getAdreca(),
                usuari.getConsentimentRGPD(),
                Date.valueOf(usuari.getDataRegistre()),
                usuari.getEstatCompte(),
                usuari.getIdUsuari()
        );
    }

    public UsuariOVI getUsuariOVI(int idUsuari) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM usuariovi WHERE idusuari = ?",
                    new UsuariOVIRowMapper(),
                    idUsuari
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<UsuariOVI> getUsuariOVIs() {
        return jdbcTemplate.query(
                "SELECT * FROM usuariovi ORDER BY idusuari",
                new UsuariOVIRowMapper()
        );
    }
}
