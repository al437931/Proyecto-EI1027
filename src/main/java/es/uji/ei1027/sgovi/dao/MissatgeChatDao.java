package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.MissatgeChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class MissatgeChatDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addMissatge(MissatgeChat missatge) {
        jdbcTemplate.update(
                "INSERT INTO missatgechat (idmissatge, idseleccion, datahora, emissor, nomemisor, missatge) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                missatge.getIdMissatge(),
                missatge.getIdSeleccion(),
                Timestamp.valueOf(missatge.getDataHora()),
                missatge.getEmissor(),
                missatge.getNomEmisor(),
                missatge.getMissatge()
        );
    }

    public List<MissatgeChat> getMissatgesBySeleccion(int idSeleccion) {
        return jdbcTemplate.query(
                "SELECT * FROM missatgechat WHERE idseleccion = ? ORDER BY datahora ASC",
                new MissatgeChatRowMapper(),
                idSeleccion
        );
    }

    /** Retorna totes les seleccions que tenen missatges per a un usuari o assistent */
    public List<MissatgeChat> getAllMissatges() {
        return jdbcTemplate.query(
                "SELECT * FROM missatgechat ORDER BY datahora DESC",
                new MissatgeChatRowMapper()
        );
    }

    public int getNextId() {
        Integer max = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(idmissatge), 0) FROM missatgechat",
                Integer.class
        );
        return (max != null ? max : 0) + 1;
    }
}
