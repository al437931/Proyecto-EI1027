package es.uji.ei1027.sgovi.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class SgoviControllerAdvice {

    // Tracta excepcions pròpies de l'aplicació (accés no autoritzat, etc.)
    @ExceptionHandler(value = SgoviException.class)
    public ModelAndView handleSgoviException(SgoviException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorName", ex.getErrorName());
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    // Tracta qualsevol altra excepció no esperada
    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorName", "Error inesperat");
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
