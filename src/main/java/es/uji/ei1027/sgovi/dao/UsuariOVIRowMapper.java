package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuariOVI;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class UsuariOVIRowMapper implements RowMapper<UsuariOVI> {
    public UsuariOVI mapRow(ResultSet rs, int rowNum) throws SQLException {
        UsuariOVI usuariOVI = new UsuariOVI();
        usuariOVI.setIdUsuari(rs.getInt("idusuari"));
        usuariOVI.setNom(rs.getString("nom"));
        usuariOVI.setCognoms(rs.getString("cognoms"));
        usuariOVI.setEmail(rs.getString("email"));
        usuariOVI.setTelefon(rs.getString("telefon"));
        usuariOVI.setAdreca(rs.getString("adreca"));
        usuariOVI.setConsentimentRGPD(rs.getBoolean("consentimentrgpd"));
        usuariOVI.setDataRegistre(rs.getDate("dataregistre").toLocalDate());
        usuariOVI.setEstatCompte(rs.getString("estatcompte"));
        return usuariOVI;
    }

}
