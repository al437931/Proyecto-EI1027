package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.RegistreContracte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

@Repository
public class RegistreContracteDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addRegistreContracte(RegistreContracte registre) {
        jdbcTemplate.update(
                "INSERT INTO registrecontracte (idcontracte, idassistent, idrequest, datainici, datafi, pdfcontracte) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                registre.getIdContracte(),
                registre.getIdAssistent(),
                registre.getIdRequest(),
                Date.valueOf(registre.getDataInici()),
                registre.getDataFi() != null ? Date.valueOf(registre.getDataFi()) : null,
                registre.getPdfContracte()
        );
    }

    public void deleteRegistreContracte(int idContracte) {
        jdbcTemplate.update(
                "DELETE FROM registrecontracte WHERE idcontracte = ?",
                idContracte
        );
    }

    public void updateRegistreContracte(RegistreContracte registre) {
        jdbcTemplate.update(
                "UPDATE registrecontracte SET idassistent = ?, idrequest = ?, datainici = ?, datafi = ?, pdfcontracte = ? " +
                        "WHERE idcontracte = ?",
                registre.getIdAssistent(),
                registre.getIdRequest(),
                Date.valueOf(registre.getDataInici()),
                registre.getDataFi() != null ? Date.valueOf(registre.getDataFi()) : null,
                registre.getPdfContracte(),
                registre.getIdContracte()
        );
    }

    public RegistreContracte getRegistreContracte(int idContracte) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM registrecontracte WHERE idcontracte = ?",
                    new RegistreContracteRowMapper(),
                    idContracte
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<RegistreContracte> getRegistresContracte() {
        return jdbcTemplate.query(
                "SELECT * FROM registrecontracte ORDER BY idcontracte",
                new RegistreContracteRowMapper()
        );
    }
}
