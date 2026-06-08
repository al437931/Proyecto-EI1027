package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.MissatgeChat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MissatgeChatRowMapper implements RowMapper<MissatgeChat> {
    @Override
    public MissatgeChat mapRow(ResultSet rs, int rowNum) throws SQLException {
        MissatgeChat m = new MissatgeChat();
        m.setIdMissatge(rs.getInt("idmissatge"));
        m.setIdSeleccion(rs.getInt("idseleccion"));
        m.setDataHora(rs.getTimestamp("datahora").toLocalDateTime());
        m.setEmissor(rs.getString("emissor"));
        m.setNomEmisor(rs.getString("nomemisor"));
        m.setMissatge(rs.getString("missatge"));
        return m;
    }
}
