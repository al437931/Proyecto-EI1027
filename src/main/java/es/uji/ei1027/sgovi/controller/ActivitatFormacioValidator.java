package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.ActivitatFormacio;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ActivitatFormacioValidator implements Validator {

    private static final List<String> TIPUS_VALIDS = Arrays.asList("formacio", "divulgacio");

    @Override
    public boolean supports(Class<?> cls) {
        return ActivitatFormacio.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ActivitatFormacio a = (ActivitatFormacio) obj;

        if (a.getTitol() == null || a.getTitol().trim().isEmpty()) {
            errors.rejectValue("titol", "obligatori", "El títol és obligatori.");
        } else if (a.getTitol().trim().length() > 150) {
            errors.rejectValue("titol", "massa.llarg", "El títol no pot superar els 150 caràcters.");
        }

        if (a.getData() == null) {
            errors.rejectValue("data", "obligatori", "La data és obligatòria.");
        }

        if (a.getTipus() == null || !TIPUS_VALIDS.contains(a.getTipus())) {
            errors.rejectValue("tipus", "invalid", "Cal seleccionar un tipus vàlid (formació o divulgació).");
        }

        if (a.getAforament() < 1) {
            errors.rejectValue("aforament", "invalid", "L'aforament ha de ser com a mínim 1.");
        }

        if (a.getIdFormador() <= 0) {
            errors.rejectValue("idFormador", "obligatori", "Cal seleccionar un formador.");
        }

        if (a.getDescripcio() != null && a.getDescripcio().trim().length() > 500) {
            errors.rejectValue("descripcio", "massa.llarg", "La descripció no pot superar els 500 caràcters.");
        }
    }
}
