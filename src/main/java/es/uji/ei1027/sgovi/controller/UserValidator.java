package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return UserDetails.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        UserDetails user = (UserDetails) obj;

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.rejectValue("username", "obligatori", "Cal introduir l'email");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", "obligatori", "Cal introduir la contrasenya");
        }
    }
}
