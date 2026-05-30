package es.uji.ei1027.sgovi.services;

import es.uji.ei1027.sgovi.dao.AssistentPersonalDao;
import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.dao.UsuariOVIDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailValidationService {

    @Autowired
    private UsuariOVIDao usuariOVIDao;

    @Autowired
    private AssistentPersonalDao assistentPersonalDao;

    @Autowired
    private FormadorDao formadorDao;

    /**
     * Comprova si un correu ja està registrat en qualsevol taula del sistema.
     * @param email El correu a comprovar.
     * @param roleToIgnore (Opcional) El rol actual ("usuari", "assistent", "formador") per ignorar el propi registre en updates.
     * @param idToIgnore (Opcional) L'ID del registre actual que s'està editant.
     * @return true si ja està en ús per una altra persona.
     */
    public boolean isEmailTaken(String email, String roleToIgnore, Integer idToIgnore) {
        if (email == null || email.trim().isEmpty()) return false;
        
        // El correu del tècnic sempre està reservat
        if ("tecnic@ovi.es".equalsIgnoreCase(email.trim())) return true;

        boolean usedByUsuari = usuariOVIDao.getUsuariOVIs().stream()
                .anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email) && 
                        !("usuari".equals(roleToIgnore) && idToIgnore != null && u.getIdUsuari() == idToIgnore));
        if (usedByUsuari) return true;

        boolean usedByAssistent = assistentPersonalDao.getAssistentsPersonals().stream()
                .anyMatch(a -> a.getEmail() != null && a.getEmail().equalsIgnoreCase(email) &&
                        !("assistent".equals(roleToIgnore) && idToIgnore != null && a.getIdAssistent() == idToIgnore));
        if (usedByAssistent) return true;

        boolean usedByFormador = formadorDao.getFormadors().stream()
                .anyMatch(f -> f.getEmail() != null && f.getEmail().equalsIgnoreCase(email) &&
                        !("formador".equals(roleToIgnore) && idToIgnore != null && f.getIdFormador() == idToIgnore));
        
        return usedByFormador;
    }
}
