package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.RegistreContracteDao;
import es.uji.ei1027.sgovi.model.RegistreContracte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registrecontracte")
public class RegistreContracteController {

    private RegistreContracteDao registreContracteDao;

    @Autowired
    public void setRegistreContracteDao(RegistreContracteDao registreContracteDao) {
        this.registreContracteDao = registreContracteDao;
    }

    @GetMapping("/list")
    public String listRegistres(Model model) {
        model.addAttribute("registres", registreContracteDao.getRegistresContracte());
        return "registrecontracte/list";
    }

    @GetMapping("/add")
    public String addRegistreForm(Model model) {
        model.addAttribute("registreContracte", new RegistreContracte());
        return "registrecontracte/add";
    }

    @PostMapping("/add")
    public String addRegistre(@ModelAttribute("registreContracte") RegistreContracte registre,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "registrecontracte/add";
        }
        registreContracteDao.addRegistreContracte(registre);
        return "redirect:/registrecontracte/list";
    }

    @GetMapping("/update/{id}")
    public String updateRegistreForm(@PathVariable int id, Model model) {
        model.addAttribute("registreContracte", registreContracteDao.getRegistreContracte(id));
        return "registrecontracte/update";
    }

    @PostMapping("/update")
    public String updateRegistre(@ModelAttribute("registreContracte") RegistreContracte registre,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "registrecontracte/update";
        }
        registreContracteDao.updateRegistreContracte(registre);
        return "redirect:/registrecontracte/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteRegistre(@PathVariable int id) {
        registreContracteDao.deleteRegistreContracte(id);
        return "redirect:/registrecontracte/list";
    }
}
