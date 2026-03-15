package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Formador;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class FormadorRowMapper implements RowMapper<Formador> {
    public Formador mapRow(ResultSet rs, int rowNum) throws SQLException {
        Formador formador = new Formador();
        formador.setIdFormador(rs.getInt("idformador"));
        formador.setNom(rs.getString("nom"));
        formador.setCognoms(rs.getString("cognoms"));
        formador.setEspecialitat(rs.getString("especialitat"));
        formador.setEmail(rs.getString("email"));
        return formador;
    }
}
