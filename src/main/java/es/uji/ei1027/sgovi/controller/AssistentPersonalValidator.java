package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.AssistentPersonal;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AssistentPersonalValidator implements Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s'-]+$");
    private static final List<String> TIPUS_VALIDS = Arrays.asList("PAP", "PATI");

    private boolean isRegistre; // true si és un formulari de registre (password obligatori)

    public AssistentPersonalValidator(boolean isRegistre) {
        this.isRegistre = isRegistre;
    }

    @Override
    public boolean supports(Class<?> cls) {
        return AssistentPersonal.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        AssistentPersonal a = (AssistentPersonal) obj;

        // Nom obligatori
        if (a.getNom() == null || a.getNom().trim().isEmpty()) {
            errors.rejectValue("nom", "obligatori", "El nom és obligatori.");
        } else if (a.getNom().trim().length() > 50) {
            errors.rejectValue("nom", "massa.llarg", "El nom no pot superar els 50 caràcters.");
        } else if (!NAME_PATTERN.matcher(a.getNom().trim()).matches()) {
            errors.rejectValue("nom", "format.invalid", "El nom només pot contindre lletres.");
        }

        // Cognoms obligatoris
        if (a.getCognoms() == null || a.getCognoms().trim().isEmpty()) {
            errors.rejectValue("cognoms", "obligatori", "Els cognoms són obligatoris.");
        } else if (a.getCognoms().trim().length() > 80) {
            errors.rejectValue("cognoms", "massa.llarg", "Els cognoms no poden superar els 80 caràcters.");
        } else if (!NAME_PATTERN.matcher(a.getCognoms().trim()).matches()) {
            errors.rejectValue("cognoms", "format.invalid", "Els cognoms només poden contindre lletres.");
        }

        // Email obligatori
        if (a.getEmail() == null || a.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatori", "El correu electrònic és obligatori.");
        } else if (!EMAIL_PATTERN.matcher(a.getEmail().trim()).matches()) {
            errors.rejectValue("email", "format.invalid", "El format del correu electrònic no és vàlid.");
        }

        // Telèfon: exactament 9 dígits si s'indica
        if (a.getTelefon() != null && !a.getTelefon().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(a.getTelefon().trim()).matches()) {
                errors.rejectValue("telefon", "format.invalid", "El telèfon ha de tindre exactament 9 dígits numèrics.");
            }
        }

        // Tipus assistent obligatori (PAP o PATI)
        if (a.getTipusAssistent() == null || !TIPUS_VALIDS.contains(a.getTipusAssistent())) {
            errors.rejectValue("tipusAssistent", "obligatori", "Cal seleccionar el tipus d'assistent (PAP o PATI).");
        }

        // Password obligatòria en registre
        if (isRegistre) {
            if (a.getPassword() == null || a.getPassword().trim().isEmpty()) {
                errors.rejectValue("password", "obligatori", "La contrasenya és obligatòria.");
            } else if (a.getPassword().trim().length() < 6) {
                errors.rejectValue("password", "massa.curt", "La contrasenya ha de tindre almenys 6 caràcters.");
            }
        }

        // Formació (opcional, max 200)
        if (a.getFormacio() != null && a.getFormacio().trim().length() > 200) {
            errors.rejectValue("formacio", "massa.llarg", "La formació no pot superar els 200 caràcters.");
        }

        // Experiència (opcional, max 200)
        if (a.getExperiencia() != null && a.getExperiencia().trim().length() > 200) {
            errors.rejectValue("experiencia", "massa.llarg", "L'experiència no pot superar els 200 caràcters.");
        }
    }
}
