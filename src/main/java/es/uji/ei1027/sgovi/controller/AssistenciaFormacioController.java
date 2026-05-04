package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.AssistenciaFormacioDao;
import es.uji.ei1027.sgovi.model.AssistenciaFormacio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/assistenciaformacio")
public class AssistenciaFormacioController {

    private AssistenciaFormacioDao assistenciaFormacioDao;

    @Autowired
    public void setAssistenciaFormacioDao(AssistenciaFormacioDao assistenciaFormacioDao) {
        this.assistenciaFormacioDao = assistenciaFormacioDao;
    }

    @GetMapping("/list")
    public String listAssistencies(Model model) {
        model.addAttribute("assistencies", assistenciaFormacioDao.getAssistenciesFormacio());
        return "assistenciaformacio/list";
    }

    @GetMapping("/add")
    public String addAssistenciaForm(Model model) {
        model.addAttribute("assistenciaFormacio", new AssistenciaFormacio());
        return "assistenciaformacio/add";
    }

    @PostMapping("/add")
    public String addAssistencia(@ModelAttribute("assistenciaFormacio") AssistenciaFormacio assistencia,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "assistenciaformacio/add";
        }
        assistenciaFormacioDao.addAssistenciaFormacio(assistencia);
        return "redirect:/assistenciaformacio/list";
    }

    @GetMapping("/update/{id}")
    public String updateAssistenciaForm(@PathVariable int id, Model model) {
        model.addAttribute("assistenciaFormacio", assistenciaFormacioDao.getAssistenciaFormacio(id));
        return "assistenciaformacio/update";
    }

    @PostMapping("/update")
    public String updateAssistencia(@ModelAttribute("assistenciaFormacio") AssistenciaFormacio assistencia,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "assistenciaformacio/update";
        }
        assistenciaFormacioDao.updateAssistenciaFormacio(assistencia);
        return "redirect:/assistenciaformacio/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteAssistencia(@PathVariable int id) {
        assistenciaFormacioDao.deleteAssistenciaFormacio(id);
        return "redirect:/assistenciaformacio/list";
    }
}
