package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ComunicacioUsuariOVIPAPDao;
import es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/comunicacio")
public class ComunicacioUsuariOVIPAPController {

    private ComunicacioUsuariOVIPAPDao comunicacioDao;

    @Autowired
    public void setComunicacioUsuariOVIPAPDao(ComunicacioUsuariOVIPAPDao comunicacioDao) {
        this.comunicacioDao = comunicacioDao;
    }

    private UsuariOVI getTecnicSession(HttpSession session) {
        UsuariOVI u = (UsuariOVI) session.getAttribute("usuariLogat");
        if (u == null || !"tecnic".equals(u.getRol())) return null;
        return u;
    }

    @GetMapping("/list")
    public String listComunicacions(@RequestParam(value = "cerca", required = false) String cerca, HttpSession session, Model model) {
        UsuariOVI tecnic = getTecnicSession(session);
        if (tecnic == null) {
            session.setAttribute("nextUrl", "/comunicacio/list");
            return "redirect:/login";
        }
        
        java.util.List<ComunicacioUsuariOVIPAP> comunicacions = comunicacioDao.getComunicacions();
        String cercaLower = (cerca != null) ? cerca.toLowerCase() : null;

        if (cercaLower != null && !cercaLower.trim().isEmpty()) {
            comunicacions = comunicacions.stream().filter(u -> {
                String assumpte = u.getAssumpte() != null ? u.getAssumpte().toLowerCase() : "";
                String destinatari = u.getDestinatari() != null ? u.getDestinatari().toLowerCase() : "";
                return assumpte.contains(cercaLower) || destinatari.contains(cercaLower);
            }).toList();
        }

        java.util.List<ComunicacioUsuariOVIPAP> mutableComunicacions = new java.util.ArrayList<>(comunicacions);
        mutableComunicacions.sort((a, b) -> Integer.compare(b.getIdComunicacio(), a.getIdComunicacio()));

        model.addAttribute("comunicacions", mutableComunicacions);
        model.addAttribute("cerca", cerca);
        model.addAttribute("usuariLogat", tecnic);
        return "comunicacio/list";
    }

    @GetMapping("/add")
    public String addComunicacioForm(HttpSession session, Model model) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        ComunicacioUsuariOVIPAP c = new ComunicacioUsuariOVIPAP();
        c.setDataHora(LocalDateTime.now());
        c.setEmissor("tecnic@ovi.es");
        model.addAttribute("comunicacio", c);
        return "comunicacio/add";
    }

    @PostMapping("/add")
    public String addComunicacio(@ModelAttribute("comunicacio") ComunicacioUsuariOVIPAP comunicacio,
                                 BindingResult bindingResult, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";

        // Validacions
        if (comunicacio.getDestinatari() == null || comunicacio.getDestinatari().trim().isEmpty()) {
            bindingResult.rejectValue("destinatari", "obligatori", "El destinatari és obligatori.");
        }
        if (comunicacio.getAssumpte() == null || comunicacio.getAssumpte().trim().isEmpty()) {
            bindingResult.rejectValue("assumpte", "obligatori", "L'assumpte és obligatori.");
        }
        if (comunicacio.getMissatge() == null || comunicacio.getMissatge().trim().isEmpty()) {
            bindingResult.rejectValue("missatge", "obligatori", "El missatge és obligatori.");
        }

        if (bindingResult.hasErrors()) {
            return "comunicacio/add";
        }

        comunicacio.setIdComunicacio(comunicacioDao.getNextId());
        comunicacio.setDataHora(LocalDateTime.now());
        comunicacio.setEmissor("tecnic@ovi.es");
        if (comunicacio.getTipusComunicacio() == null || comunicacio.getTipusComunicacio().trim().isEmpty()) {
            comunicacio.setTipusComunicacio("general");
        }

        comunicacioDao.addComunicacio(comunicacio);
        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Comunicació enviada correctament (simulada).");
        return "redirect:/comunicacio/list";
    }

    @GetMapping("/detall/{id}")
    public String detallComunicacio(@PathVariable int id, HttpSession session, Model model) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        ComunicacioUsuariOVIPAP c = comunicacioDao.getComunicacio(id);
        if (c == null) throw new SgoviException("Comunicació no trobada", "Error");
        model.addAttribute("comunicacio", c);
        return "comunicacio/detall";
    }

    @GetMapping("/delete/{id}")
    public String deleteComunicacio(@PathVariable int id, HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        comunicacioDao.deleteComunicacio(id);
        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Comunicació eliminada.");
        return "redirect:/comunicacio/list";
    }
}
