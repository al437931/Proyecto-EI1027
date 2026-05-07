package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuariOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDaoImpl implements UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public UsuariOVI loadUserByUsernameAndPassword(String username, String password) {
        try {
            UsuariOVI usuari = jdbcTemplate.queryForObject(
                    "SELECT * FROM usuariovi WHERE email = ?",
                    new UsuariOVIRowMapper(),
                    username.trim()
            );
            if (usuari == null) return null;
            // Comprovar contrasenya (text pla per ara, sense encriptació)
            if (password != null && password.equals(usuari.getPassword())) {
                return usuari;
            }
            return null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
