package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.AssistentPersonalDao;
import es.uji.ei1027.sgovi.model.AssistentPersonal;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

@Controller
@RequestMapping("/assistent")
public class AssistentPanelController {

    private AssistentPersonalDao assistentPersonalDao;
    private es.uji.ei1027.sgovi.dao.SeleccionDao seleccionDao;
    private es.uji.ei1027.sgovi.dao.RegistreContracteDao registreContracteDao;
    private es.uji.ei1027.sgovi.dao.APRequestDao apRequestDao;
    private es.uji.ei1027.sgovi.dao.UsuariOVIDao usuariOVIDao;

    @Autowired
    public void setAssistentPersonalDao(AssistentPersonalDao assistentPersonalDao) {
        this.assistentPersonalDao = assistentPersonalDao;
    }

    @Autowired
    public void setSeleccionDao(es.uji.ei1027.sgovi.dao.SeleccionDao seleccionDao) {
        this.seleccionDao = seleccionDao;
    }

    @Autowired
    public void setRegistreContracteDao(es.uji.ei1027.sgovi.dao.RegistreContracteDao registreContracteDao) {
        this.registreContracteDao = registreContracteDao;
    }

    @Autowired
    public void setApRequestDao(es.uji.ei1027.sgovi.dao.APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @Autowired
    public void setUsuariOVIDao(es.uji.ei1027.sgovi.dao.UsuariOVIDao usuariOVIDao) {
        this.usuariOVIDao = usuariOVIDao;
    }

    private UsuariOVI getAssistentSession(HttpSession session) {
        UsuariOVI u = (UsuariOVI) session.getAttribute("usuariLogat");
        if (u == null || !"assistent".equals(u.getRol())) return null;
        return u;
    }

    // GET /assistent/propostes - Veure les propostes de treball
    @GetMapping("/propostes")
    public String propostes(HttpSession session, Model model) {
        UsuariOVI sessionUser = getAssistentSession(session);
        if (sessionUser == null) {
            session.setAttribute("nextUrl", "/assistent/propostes");
            return "redirect:/login";
        }

        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(sessionUser.getIdUsuari());
        model.addAttribute("assistent", assistent);
        model.addAttribute("usuariLogat", sessionUser);
        
        // Obtenir seleccions (propostes de treball)
        java.util.List<es.uji.ei1027.sgovi.model.Seleccion> seleccions = seleccionDao.getSeleccionsByAssistent(assistent.getIdAssistent());
        
        // Populate nomUsuariComplet for each seleccion
        for (es.uji.ei1027.sgovi.model.Seleccion seleccion : seleccions) {
            if (seleccion.getIdRequest() > 0) {
                es.uji.ei1027.sgovi.model.APRequest apRequest = apRequestDao.getAPRequest(seleccion.getIdRequest());
                if (apRequest != null && apRequest.getIdUsuari() > 0) {
                    es.uji.ei1027.sgovi.model.UsuariOVI usuari = usuariOVIDao.getUsuariOVI(apRequest.getIdUsuari());
                    if (usuari != null) {
                        seleccion.setNomUsuariComplet(usuari.getNom() + " " + (usuari.getCognoms() != null ? usuari.getCognoms() : ""));
                    } else {
                        seleccion.setNomUsuariComplet("Usuari Desconegut");
                    }
                } else {
                    seleccion.setNomUsuariComplet("Sol·licitud sense usuari");
                }
            } else {
                seleccion.setNomUsuariComplet("Sense sol·licitud");
            }
        }
        
        model.addAttribute("seleccions", seleccions);

        return "assistent/propostes";
    }

    // GET /assistent/perfil - Veure i editar el perfil
    @GetMapping("/perfil")
    public String veurePerfil(HttpSession session, Model model) {
        UsuariOVI sessionUser = getAssistentSession(session);
        if (sessionUser == null) {
            session.setAttribute("nextUrl", "/assistent/perfil");
            return "redirect:/login";
        }

        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(sessionUser.getIdUsuari());
        model.addAttribute("assistent", assistent);
        model.addAttribute("usuariLogat", sessionUser);

        return "assistent/perfil";
    }

    // GET /assistent/historial - Veure l'historial de contractes
    @GetMapping("/historial")
    public String historial(HttpSession session, Model model) {
        UsuariOVI sessionUser = getAssistentSession(session);
        if (sessionUser == null) {
            session.setAttribute("nextUrl", "/assistent/historial");
            return "redirect:/login";
        }

        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(sessionUser.getIdUsuari());
        model.addAttribute("assistent", assistent);
        model.addAttribute("usuariLogat", sessionUser);
        
        // Obtenir historial de contractes
        java.util.List<es.uji.ei1027.sgovi.model.RegistreContracte> contractes = registreContracteDao.getContractesByAssistent(assistent.getIdAssistent());
        
        for (es.uji.ei1027.sgovi.model.RegistreContracte contracte : contractes) {
            if (contracte.getIdRequest() != null && contracte.getIdRequest() > 0) {
                es.uji.ei1027.sgovi.model.APRequest req = apRequestDao.getAPRequest(contracte.getIdRequest());
                if (req != null && req.getIdUsuari() > 0) {
                    es.uji.ei1027.sgovi.model.UsuariOVI usuari = usuariOVIDao.getUsuariOVI(req.getIdUsuari());
                    if (usuari != null) {
                        contracte.setNomUsuariComplet(usuari.getNom() + " " + (usuari.getCognoms() != null ? usuari.getCognoms() : ""));
                    } else {
                        contracte.setNomUsuariComplet("Usuari Desconegut");
                    }
                } else {
                    contracte.setNomUsuariComplet("Sol·licitud sense usuari");
                }
            } else {
                contracte.setNomUsuariComplet("Sense sol·licitud");
            }
        }
        
        model.addAttribute("contractes", contractes);

        return "assistent/historial";
    }

    // POST /assistent/seleccion/{idSeleccion}/acceptar
    @PostMapping("/seleccion/{idSeleccion}/acceptar")
    public String acceptarSeleccion(@PathVariable int idSeleccion, HttpSession session, RedirectAttributes redirectAttributes) {
        UsuariOVI sessionUser = getAssistentSession(session);
        if (sessionUser == null) return "redirect:/login";

        es.uji.ei1027.sgovi.model.Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(sessionUser.getIdUsuari());
        
        if (seleccion != null && seleccion.getIdAssistent() == assistent.getIdAssistent()) {
            if ("Pendent Assistent".equals(seleccion.getEstat())) {
                seleccion.setEstat("Acceptada");
                redirectAttributes.addFlashAttribute("missatgeExitFlash", "Heu acceptat la proposta correctament. Ja esteu llestos per formalitzar el contracte.");
            } else if ("pendent_ambdos".equals(seleccion.getEstat())) {
                seleccion.setEstat("Pendent Usuari");
                redirectAttributes.addFlashAttribute("missatgeExitFlash", "Heu acceptat la proposta del tècnic. Ara falta l'aprovació de l'usuari.");
            }
            seleccionDao.updateSeleccion(seleccion);
        }
        return "redirect:/assistent/propostes";
    }

    // POST /assistent/seleccion/{idSeleccion}/rebutjar
    @PostMapping("/seleccion/{idSeleccion}/rebutjar")
    public String rebutjarSeleccion(@PathVariable int idSeleccion, HttpSession session, RedirectAttributes redirectAttributes) {
        UsuariOVI sessionUser = getAssistentSession(session);
        if (sessionUser == null) return "redirect:/login";

        es.uji.ei1027.sgovi.model.Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(sessionUser.getIdUsuari());
        
        if (seleccion != null && seleccion.getIdAssistent() == assistent.getIdAssistent()) {
            seleccion.setEstat("Rebutjada");
            seleccionDao.updateSeleccion(seleccion);
            redirectAttributes.addFlashAttribute("missatgeExitFlash", "Heu rebutjat la proposta correctament.");
        }
        return "redirect:/assistent/propostes";
    }

    @PostMapping("/perfil")
    public String actualitzarPerfil(@ModelAttribute("assistent") AssistentPersonal assistent,
                                    BindingResult bindingResult,
                                    HttpSession session, Model model,
                                    RedirectAttributes redirectAttributes) {
        UsuariOVI sessionUser = getAssistentSession(session);
        if (sessionUser == null) return "redirect:/login";

        // Validacions bàsiques
        Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s'-]+$");
        Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9}$");

        if (assistent.getNom() == null || assistent.getNom().trim().isEmpty()) {
            bindingResult.rejectValue("nom", "obligatori", "El nom és obligatori.");
        } else if (!NAME_PATTERN.matcher(assistent.getNom().trim()).matches()) {
            bindingResult.rejectValue("nom", "format.invalid", "El nom només pot contindre lletres.");
        }

        if (assistent.getCognoms() == null || assistent.getCognoms().trim().isEmpty()) {
            bindingResult.rejectValue("cognoms", "obligatori", "Els cognoms són obligatoris.");
        }

        if (assistent.getTelefon() != null && !assistent.getTelefon().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(assistent.getTelefon().trim()).matches()) {
                bindingResult.rejectValue("telefon", "format.invalid", "El telèfon ha de tindre 9 dígits.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuariLogat", sessionUser);
            return "assistent/perfil";
        }

        // Actualitzar en BD
        AssistentPersonal existent = assistentPersonalDao.getAssistentPersonal(sessionUser.getIdUsuari());
        existent.setNom(assistent.getNom());
        existent.setCognoms(assistent.getCognoms());
        existent.setTelefon(assistent.getTelefon());
        existent.setFormacio(assistent.getFormacio());
        existent.setExperiencia(assistent.getExperiencia());
        existent.setDisponibilitat(assistent.getDisponibilitat());
        assistentPersonalDao.updateAssistentPersonal(existent);

        // Actualitzar sessió
        sessionUser.setNom(assistent.getNom());
        sessionUser.setCognoms(assistent.getCognoms());
        session.setAttribute("usuariLogat", sessionUser);

        redirectAttributes.addFlashAttribute("missatgeExitFlash", "El perfil s'ha actualitzat correctament.");
        return "redirect:/assistent/perfil";
    }
}
