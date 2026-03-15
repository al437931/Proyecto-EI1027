package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UsuariOVIDao;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuariovi")
public class UsuariOVIController {

    private UsuariOVIDao usuariOVIDao;

    @Autowired
    public void setUsuariOVIDao(UsuariOVIDao usuariOVIDao) {
        this.usuariOVIDao = usuariOVIDao;
    }

    @GetMapping("/list")
    public String listUsuariOVIs(Model model) {
        model.addAttribute("usuaris", usuariOVIDao.getUsuariOVIs());
        return "usuariovi/list";
    }

    @GetMapping("/add")
    public String addUsuariForm(Model model) {
        model.addAttribute("usuariOVI", new UsuariOVI());
        return "usuariovi/add";
    }

    @PostMapping("/add")
    public String addUsuari(@ModelAttribute("usuariOVI") UsuariOVI usuariOVI,
                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "usuariovi/add";
        }

        usuariOVIDao.addUsuariOVI(usuariOVI);
        return "redirect:/usuariovi/list";
    }

    @GetMapping("/update/{id}")
    public String updateUsuariForm(@PathVariable int id, Model model) {
        model.addAttribute("usuariOVI", usuariOVIDao.getUsuariOVI(id));
        return "usuariovi/update";
    }

    @PostMapping("/update")
    public String updateUsuari(@ModelAttribute("usuariOVI") UsuariOVI usuariOVI,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "usuariovi/update";
        }

        usuariOVIDao.updateUsuariOVI(usuariOVI);
        return "redirect:/usuariovi/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUsuari(@PathVariable int id) {
        usuariOVIDao.deleteUsuariOVI(id);
        return "redirect:/usuariovi/list";
    }
}
