package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.Formador;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

public class FormadorValidator implements Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s'-]+$");

    @Override
    public boolean supports(Class<?> cls) {
        return Formador.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Formador f = (Formador) obj;

        if (f.getNom() == null || f.getNom().trim().isEmpty()) {
            errors.rejectValue("nom", "obligatori", "El nom és obligatori.");
        } else if (f.getNom().trim().length() > 50) {
            errors.rejectValue("nom", "massa.llarg", "El nom no pot superar els 50 caràcters.");
        } else if (!NAME_PATTERN.matcher(f.getNom().trim()).matches()) {
            errors.rejectValue("nom", "format.invalid", "El nom només pot contindre lletres.");
        }

        if (f.getCognoms() == null || f.getCognoms().trim().isEmpty()) {
            errors.rejectValue("cognoms", "obligatori", "Els cognoms són obligatoris.");
        } else if (f.getCognoms().trim().length() > 80) {
            errors.rejectValue("cognoms", "massa.llarg", "Els cognoms no poden superar els 80 caràcters.");
        } else if (!NAME_PATTERN.matcher(f.getCognoms().trim()).matches()) {
            errors.rejectValue("cognoms", "format.invalid", "Els cognoms només poden contindre lletres.");
        }

        if (f.getEmail() == null || f.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatori", "El correu electrònic és obligatori.");
        } else if (!EMAIL_PATTERN.matcher(f.getEmail().trim()).matches()) {
            errors.rejectValue("email", "format.invalid", "El format del correu electrònic no és vàlid.");
        }

        if (f.getEspecialitat() != null && f.getEspecialitat().trim().length() > 100) {
            errors.rejectValue("especialitat", "massa.llarg", "L'especialitat no pot superar els 100 caràcters.");
        }
    }
}
