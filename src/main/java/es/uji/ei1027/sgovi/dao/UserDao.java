package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.model.UsuariOVI;

public interface UserDao {
    // Retorna l'usuari si les credencials son correctes, null si no
    UsuariOVI loadUserByUsernameAndPassword(String username, String password);
}
