package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.AssistentPersonal;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class AssistentPersonalRowMapper implements RowMapper<AssistentPersonal> {
    public AssistentPersonal mapRow(ResultSet rs, int numRow) throws SQLException {
        AssistentPersonal assistentPersonal = new AssistentPersonal();
        assistentPersonal.setIdAssistent(rs.getInt("idassistent"));
        assistentPersonal.setNom(rs.getString("nom"));
        assistentPersonal.setCognoms(rs.getString("cognoms"));
        assistentPersonal.setEmail(rs.getString("email"));
        assistentPersonal.setTelefon(rs.getString("telefon"));
        assistentPersonal.setFormacio(rs.getString("formacio"));
        assistentPersonal.setExperiencia(rs.getString("experiencia"));
        assistentPersonal.setDisponibilitat(rs.getString("disponibilitat"));
        assistentPersonal.setEstatAcceptat(rs.getBoolean("estatacceptat"));
        return assistentPersonal;
    }
}
