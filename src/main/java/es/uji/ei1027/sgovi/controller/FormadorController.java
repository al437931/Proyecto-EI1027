package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.model.Formador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/formador")
public class FormadorController {

    private FormadorDao formadorDao;
    private es.uji.ei1027.sgovi.services.EmailValidationService emailValidationService;

    @Autowired
    public void setFormadorDao(FormadorDao formadorDao) {
        this.formadorDao = formadorDao;
    }

    @Autowired
    public void setEmailValidationService(es.uji.ei1027.sgovi.services.EmailValidationService emailValidationService) {
        this.emailValidationService = emailValidationService;
    }

    @GetMapping("/list")
    public String listFormadors(Model model) {
        model.addAttribute("formadors", formadorDao.getFormadors());
        return "formador/list";
    }

    @GetMapping("/add")
    public String addFormadorForm(Model model) {
        model.addAttribute("formador", new Formador());
        return "formador/add";
    }

    @PostMapping("/add")
    public String addFormador(@ModelAttribute("formador") Formador formador,
                              BindingResult bindingResult) {
        if (emailValidationService.isEmailTaken(formador.getEmail(), null, null)) {
            bindingResult.rejectValue("email", "email.duplicat",
                    "Aquest correu electrònic ja està registrat en el sistema (com a usuari, assistent o formador).");
        }

        if (bindingResult.hasErrors()) {
            return "formador/add";
        }
        formadorDao.addFormador(formador);
        return "redirect:/formador/list";
    }

    @GetMapping("/update/{id}")
    public String updateFormadorForm(@PathVariable int id, Model model) {
        model.addAttribute("formador", formadorDao.getFormador(id));
        return "formador/update";
    }

    @PostMapping("/update")
    public String updateFormador(@ModelAttribute("formador") Formador formador,
                                 BindingResult bindingResult) {
        if (emailValidationService.isEmailTaken(formador.getEmail(), "formador", formador.getIdFormador())) {
            bindingResult.rejectValue("email", "email.duplicat",
                    "Aquest correu electrònic ja està en ús per una altra persona.");
        }

        if (bindingResult.hasErrors()) {
            return "formador/update";
        }
        formadorDao.updateFormador(formador);
        return "redirect:/formador/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteFormador(@PathVariable int id) {
        formadorDao.deleteFormador(id);
        return "redirect:/formador/list";
    }
}
