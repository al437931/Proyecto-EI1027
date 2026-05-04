package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.model.Seleccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/seleccion")
public class SeleccionController {

    private SeleccionDao seleccionDao;

    @Autowired
    public void setSeleccionDao(SeleccionDao seleccionDao) {
        this.seleccionDao = seleccionDao;
    }

    @GetMapping("/list")
    public String listSeleccions(Model model) {
        model.addAttribute("seleccions", seleccionDao.getSeleccions());
        return "seleccion/list";
    }

    @GetMapping("/add")
    public String addSeleccionForm(Model model) {
        model.addAttribute("seleccion", new Seleccion());
        return "seleccion/add";
    }

    @PostMapping("/add")
    public String addSeleccion(@ModelAttribute("seleccion") Seleccion seleccion,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "seleccion/add";
        }
        seleccionDao.addSeleccion(seleccion);
        return "redirect:/seleccion/list";
    }

    @GetMapping("/update/{id}")
    public String updateSeleccionForm(@PathVariable int id, Model model) {
        model.addAttribute("seleccion", seleccionDao.getSeleccion(id));
        return "seleccion/update";
    }

    @PostMapping("/update")
    public String updateSeleccion(@ModelAttribute("seleccion") Seleccion seleccion,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "seleccion/update";
        }
        seleccionDao.updateSeleccion(seleccion);
        return "redirect:/seleccion/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteSeleccion(@PathVariable int id) {
        seleccionDao.deleteSeleccion(id);
        return "redirect:/seleccion/list";
    }
}
