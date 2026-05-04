package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ComunicacioUsuariOVIPAPDao;
import es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comunicacio")
public class ComunicacioUsuariOVIPAPController {

    private ComunicacioUsuariOVIPAPDao comunicacioDao;

    @Autowired
    public void setComunicacioUsuariOVIPAPDao(ComunicacioUsuariOVIPAPDao comunicacioDao) {
        this.comunicacioDao = comunicacioDao;
    }

    @GetMapping("/list")
    public String listComunicacions(Model model) {
        model.addAttribute("comunicacions", comunicacioDao.getComunicacions());
        return "comunicacio/list";
    }

    @GetMapping("/add")
    public String addComunicacioForm(Model model) {
        model.addAttribute("comunicacio", new ComunicacioUsuariOVIPAP());
        return "comunicacio/add";
    }

    @PostMapping("/add")
    public String addComunicacio(@ModelAttribute("comunicacio") ComunicacioUsuariOVIPAP comunicacio,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "comunicacio/add";
        }
        comunicacioDao.addComunicacio(comunicacio);
        return "redirect:/comunicacio/list";
    }

    @GetMapping("/update/{id}")
    public String updateComunicacioForm(@PathVariable int id, Model model) {
        model.addAttribute("comunicacio", comunicacioDao.getComunicacio(id));
        return "comunicacio/update";
    }

    @PostMapping("/update")
    public String updateComunicacio(@ModelAttribute("comunicacio") ComunicacioUsuariOVIPAP comunicacio,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "comunicacio/update";
        }
        comunicacioDao.updateComunicacio(comunicacio);
        return "redirect:/comunicacio/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteComunicacio(@PathVariable int id) {
        comunicacioDao.deleteComunicacio(id);
        return "redirect:/comunicacio/list";
    }
}
