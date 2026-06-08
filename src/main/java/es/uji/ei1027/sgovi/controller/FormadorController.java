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
    public String listFormadors(@RequestParam(value = "cerca", required = false) String cerca, Model model) {
        java.util.List<Formador> formadors = formadorDao.getFormadors();
        String cercaLower = (cerca != null) ? cerca.toLowerCase() : null;

        if (cercaLower != null && !cercaLower.trim().isEmpty()) {
            formadors = formadors.stream().filter(u -> {
                String nom = u.getNom() != null ? u.getNom().toLowerCase() : "";
                String cognoms = u.getCognoms() != null ? u.getCognoms().toLowerCase() : "";
                String email = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                String especialitat = u.getEspecialitat() != null ? u.getEspecialitat().toLowerCase() : "";
                return nom.contains(cercaLower) || cognoms.contains(cercaLower) || email.contains(cercaLower) || especialitat.contains(cercaLower);
            }).toList();
        }
        
        java.util.List<Formador> mutableFormadors = new java.util.ArrayList<>(formadors);
        mutableFormadors.sort((a, b) -> Integer.compare(b.getIdFormador(), a.getIdFormador()));

        model.addAttribute("formadors", mutableFormadors);
        model.addAttribute("cerca", cerca);
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
