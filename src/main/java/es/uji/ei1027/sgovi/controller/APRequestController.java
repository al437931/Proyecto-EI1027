package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.APRequestDao;
import es.uji.ei1027.sgovi.model.APRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aprequest")
public class APRequestController {

    private APRequestDao apRequestDao;

    @Autowired
    public void setAPRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @GetMapping("/list")
    public String listAPRequests(Model model) {
        model.addAttribute("requests", apRequestDao.getAPRequests());
        return "aprequest/list";
    }

    @GetMapping("/add")
    public String addAPRequestForm(Model model) {
        model.addAttribute("apRequest", new APRequest());
        return "aprequest/add";
    }

    @PostMapping("/add")
    public String addAPRequest(@ModelAttribute("apRequest") APRequest apRequest,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "aprequest/add";
        }
        apRequestDao.addAPRequest(apRequest);
        return "redirect:/aprequest/list";
    }

    @GetMapping("/update/{id}")
    public String updateAPRequestForm(@PathVariable int id, Model model) {
        model.addAttribute("apRequest", apRequestDao.getAPRequest(id));
        return "aprequest/update";
    }

    @PostMapping("/update")
    public String updateAPRequest(@ModelAttribute("apRequest") APRequest apRequest,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "aprequest/update";
        }
        apRequestDao.updateAPRequest(apRequest);
        return "redirect:/aprequest/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteAPRequest(@PathVariable int id) {
        apRequestDao.deleteAPRequest(id);
        return "redirect:/aprequest/list";
    }
}
