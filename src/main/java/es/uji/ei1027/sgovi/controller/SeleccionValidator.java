package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.Seleccion;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SeleccionValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Seleccion.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Seleccion s = (Seleccion) obj;

        if (s.getIdRequest() <= 0) {
            errors.rejectValue("idRequest", "obligatori", "Cal especificar una sol·licitud vàlida.");
        }

        if (s.getIdAssistent() <= 0) {
            errors.rejectValue("idAssistent", "obligatori", "Cal especificar un assistent vàlid.");
        }

        if (s.getDataProposta() == null) {
            errors.rejectValue("dataProposta", "obligatori", "La data de proposta és obligatòria.");
        }

        if (s.getEstat() == null || s.getEstat().trim().isEmpty()) {
            errors.rejectValue("estat", "obligatori", "L'estat és obligatori.");
        } else if (s.getEstat().length() > 20) {
            errors.rejectValue("estat", "massa.llarg", "L'estat no pot superar els 20 caràcters.");
        }
    }
}
