package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.UsuariOVI;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

public class UsuariOVIValidator implements Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s'-]+$");

    @Override
    public boolean supports(Class<?> cls) {
        return UsuariOVI.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        UsuariOVI u = (UsuariOVI) obj;

        // Nom obligatori i només lletres
        if (u.getNom() == null || u.getNom().trim().isEmpty()) {
            errors.rejectValue("nom", "obligatori", "El nom és obligatori.");
        } else if (u.getNom().trim().length() > 50) {
            errors.rejectValue("nom", "massa.llarg", "El nom no pot superar els 50 caràcters.");
        } else if (!NAME_PATTERN.matcher(u.getNom().trim()).matches()) {
            errors.rejectValue("nom", "format.invalid", "El nom només pot contindre lletres, espais, guions i apòstrofs.");
        }

        // Cognoms obligatoris i només lletres
        if (u.getCognoms() == null || u.getCognoms().trim().isEmpty()) {
            errors.rejectValue("cognoms", "obligatori", "Els cognoms són obligatoris.");
        } else if (u.getCognoms().trim().length() > 80) {
            errors.rejectValue("cognoms", "massa.llarg", "Els cognoms no poden superar els 80 caràcters.");
        } else if (!NAME_PATTERN.matcher(u.getCognoms().trim()).matches()) {
            errors.rejectValue("cognoms", "format.invalid", "Els cognoms només poden contindre lletres, espais, guions i apòstrofs.");
        }

        // Email obligatori i format vàlid
        if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatori", "El correu electrònic és obligatori.");
        } else if (u.getEmail().trim().length() > 100) {
            errors.rejectValue("email", "massa.llarg", "El correu no pot superar els 100 caràcters.");
        } else if (!EMAIL_PATTERN.matcher(u.getEmail().trim()).matches()) {
            errors.rejectValue("email", "format.invalid", "El format del correu electrònic no és vàlid.");
        }

        // Telèfon: exactament 9 dígits
        if (u.getTelefon() != null && !u.getTelefon().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(u.getTelefon().trim()).matches()) {
                errors.rejectValue("telefon", "format.invalid", "El telèfon ha de tindre exactament 9 dígits numèrics.");
            }
        }

        // Contrasenya obligatòria (per al registre)
        if (u.getPassword() != null) { // Només validar si el camp existeix en el formulari
            if (u.getPassword().trim().isEmpty()) {
                errors.rejectValue("password", "obligatori", "La contrasenya és obligatòria.");
            } else if (u.getPassword().trim().length() < 6) {
                errors.rejectValue("password", "massa.curt", "La contrasenya ha de tindre almenys 6 caràcters.");
            } else if (u.getPassword().trim().length() > 50) {
                errors.rejectValue("password", "massa.llarg", "La contrasenya no pot superar els 50 caràcters.");
            }
        }

        // Adreça (opcional, però amb límit)
        if (u.getAdreca() != null && u.getAdreca().trim().length() > 150) {
            errors.rejectValue("adreca", "massa.llarg", "L'adreça no pot superar els 150 caràcters.");
        }
    }
}
