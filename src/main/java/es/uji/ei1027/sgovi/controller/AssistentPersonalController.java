package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.AssistentPersonalDao;
import es.uji.ei1027.sgovi.model.AssistentPersonal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/assistentpersonal")
public class AssistentPersonalController {

    private AssistentPersonalDao assistentPersonalDao;

    @Autowired
    public void setAssistentPersonalDao(AssistentPersonalDao assistentPersonalDao) {
        this.assistentPersonalDao = assistentPersonalDao;
    }

    // LISTAR
    @GetMapping("/list")
    public String listAssistents(Model model) {
        model.addAttribute("assistents", assistentPersonalDao.getAssistentsPersonals());
        return "assistentpersonal/list";
    }

    // AÑADIR (GET)
    @GetMapping("/add")
    public String addAssistentForm(Model model) {
        model.addAttribute("assistentPersonal", new AssistentPersonal());
        return "assistentpersonal/add";
    }

    // AÑADIR (POST)
    @PostMapping("/add")
    public String addAssistent(@ModelAttribute("assistentPersonal") AssistentPersonal assistent,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "assistentpersonal/add";
        }

        assistentPersonalDao.addAssistentPersonal(assistent);
        return "redirect:/assistentpersonal/list";
    }

    // ACTUALIZAR (GET)
    @GetMapping("/update/{id}")
    public String updateAssistentForm(@PathVariable int id, Model model) {
        model.addAttribute("assistentPersonal", assistentPersonalDao.getAssistentPersonal(id));
        return "assistentpersonal/update";
    }

    // ACTUALIZAR (POST)
    @PostMapping("/update")
    public String updateAssistent(@ModelAttribute("assistentPersonal") AssistentPersonal assistent,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "assistentpersonal/update";
        }

        assistentPersonalDao.updateAssistentPersonal(assistent);
        return "redirect:/assistentpersonal/list";
    }

    // BORRAR
    @GetMapping("/delete/{id}")
    public String deleteAssistent(@PathVariable int id) {
        assistentPersonalDao.deleteAssistentPersonal(id);
        return "redirect:/assistentpersonal/list";
    }
}
