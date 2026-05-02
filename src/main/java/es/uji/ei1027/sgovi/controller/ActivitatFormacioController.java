package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ActivitatFormacioDao;
import es.uji.ei1027.sgovi.model.ActivitatFormacio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/activitatformacio")
public class ActivitatFormacioController {

    private ActivitatFormacioDao activitatFormacioDao;

    @Autowired
    public void setActivitatFormacioDao(ActivitatFormacioDao activitatFormacioDao) {
        this.activitatFormacioDao = activitatFormacioDao;
    }

    @GetMapping("/list")
    public String listActivitats(Model model) {
        model.addAttribute("activitats", activitatFormacioDao.getActivitatsFormacio());
        return "activitatformacio/list";
    }

    @GetMapping("/add")
    public String addActivitatForm(Model model) {
        model.addAttribute("activitatFormacio", new ActivitatFormacio());
        return "activitatformacio/add";
    }

    @PostMapping("/add")
    public String addActivitat(@ModelAttribute("activitatFormacio") ActivitatFormacio activitat,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "activitatformacio/add";
        }
        activitatFormacioDao.addActivitatFormacio(activitat);
        return "redirect:/activitatformacio/list";
    }

    @GetMapping("/update/{id}")
    public String updateActivitatForm(@PathVariable int id, Model model) {
        model.addAttribute("activitatFormacio", activitatFormacioDao.getActivitatFormacio(id));
        return "activitatformacio/update";
    }

    @PostMapping("/update")
    public String updateActivitat(@ModelAttribute("activitatFormacio") ActivitatFormacio activitat,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "activitatformacio/update";
        }
        activitatFormacioDao.updateActivitatFormacio(activitat);
        return "redirect:/activitatformacio/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteActivitat(@PathVariable int id) {
        activitatFormacioDao.deleteActivitatFormacio(id);
        return "redirect:/activitatformacio/list";
    }
}
