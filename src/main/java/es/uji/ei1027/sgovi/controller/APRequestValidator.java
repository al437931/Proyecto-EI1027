package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.APRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class APRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return APRequest.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        APRequest request = (APRequest) obj;

        // Validar tipus assistencia
        List<String> tipusValids = Arrays.asList("PAP", "PATI");
        if (request.getTipusAssistencia() == null ||
                !tipusValids.contains(request.getTipusAssistencia())) {
            errors.rejectValue("tipusAssistencia", "invalid",
                    "Cal seleccionar un tipus d'assistència (PAP o PATI)");
        }

        // Validar descripció
        if (request.getDescripcioNecessitats() == null ||
                request.getDescripcioNecessitats().trim().isEmpty()) {
            errors.rejectValue("descripcioNecessitats", "obligatori",
                    "Cal descriure les necessitats d'assistència");
        } else if (request.getDescripcioNecessitats().trim().length() < 10) {
            errors.rejectValue("descripcioNecessitats", "massCurt",
                    "La descripció ha de tindre almenys 10 caràcters");
        }
    }
}
