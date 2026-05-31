package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ActivitatFormacioDao;
import es.uji.ei1027.sgovi.dao.AssistenciaFormacioDao;
import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.model.ActivitatFormacio;
import es.uji.ei1027.sgovi.model.AssistenciaFormacio;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/activitatformacio")
public class ActivitatFormacioController {

    private ActivitatFormacioDao activitatFormacioDao;
    private FormadorDao formadorDao;
    private AssistenciaFormacioDao assistenciaFormacioDao;

    @Autowired
    public void setActivitatFormacioDao(ActivitatFormacioDao activitatFormacioDao) {
        this.activitatFormacioDao = activitatFormacioDao;
    }

    @Autowired
    public void setFormadorDao(FormadorDao formadorDao) {
        this.formadorDao = formadorDao;
    }

    @Autowired
    public void setAssistenciaFormacioDao(AssistenciaFormacioDao assistenciaFormacioDao) {
        this.assistenciaFormacioDao = assistenciaFormacioDao;
    }

    private UsuariOVI getTecnicSession(HttpSession session) {
        UsuariOVI u = (UsuariOVI) session.getAttribute("usuariLogat");
        if (u == null || !"tecnic".equals(u.getRol())) return null;
        return u;
    }

    private UsuariOVI getUsuariSession(HttpSession session) {
        UsuariOVI u = (UsuariOVI) session.getAttribute("usuariLogat");
        if (u == null || !"usuari".equals(u.getRol())) return null;
        return u;
    }

    // ==================== VISTA TÈCNIC (CRUD) ====================

    @GetMapping("/list")
    public String listActivitats(HttpSession session, Model model) {
        UsuariOVI tecnic = getTecnicSession(session);
        if (tecnic == null) {
            session.setAttribute("nextUrl", "/activitatformacio/list");
            return "redirect:/login";
        }

        List<ActivitatFormacio> activitats = activitatFormacioDao.getActivitatsFormacio();

        // Mapa d'inscrits per activitat
        Map<Integer, Integer> inscritsMap = new HashMap<>();
        for (ActivitatFormacio a : activitats) {
            inscritsMap.put(a.getIdActivitat(), assistenciaFormacioDao.countInscrits(a.getIdActivitat()));
        }

        model.addAttribute("activitats", activitats);
        model.addAttribute("inscritsMap", inscritsMap);
        model.addAttribute("usuariLogat", tecnic);
        return "activitatFormacio/list";
    }

    @GetMapping("/add")
    public String addActivitatForm(HttpSession session, Model model) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        model.addAttribute("activitatFormacio", new ActivitatFormacio());
        model.addAttribute("formadors", formadorDao.getFormadors());
        return "activitatFormacio/add";
    }

    @PostMapping("/add")
    public String addActivitat(@ModelAttribute("activitatFormacio") ActivitatFormacio activitat,
                               BindingResult bindingResult, Model model, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";

        ActivitatFormacioValidator validator = new ActivitatFormacioValidator();
        validator.validate(activitat, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("formadors", formadorDao.getFormadors());
            return "activitatFormacio/add";
        }

        // Generar ID
        List<ActivitatFormacio> totes = activitatFormacioDao.getActivitatsFormacio();
        int nouId = totes.stream().mapToInt(ActivitatFormacio::getIdActivitat).max().orElse(0) + 1;
        activitat.setIdActivitat(nouId);

        activitatFormacioDao.addActivitatFormacio(activitat);
        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Activitat creada correctament.");
        return "redirect:/activitatformacio/list";
    }

    @GetMapping("/update/{id}")
    public String updateActivitatForm(@PathVariable int id, HttpSession session, Model model) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        model.addAttribute("activitatFormacio", activitatFormacioDao.getActivitatFormacio(id));
        model.addAttribute("formadors", formadorDao.getFormadors());
        return "activitatFormacio/update";
    }

    @PostMapping("/update")
    public String updateActivitat(@ModelAttribute("activitatFormacio") ActivitatFormacio activitat,
                                  BindingResult bindingResult, Model model, HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";

        ActivitatFormacioValidator validator = new ActivitatFormacioValidator();
        validator.validate(activitat, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("formadors", formadorDao.getFormadors());
            return "activitatFormacio/update";
        }
        activitatFormacioDao.updateActivitatFormacio(activitat);
        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Activitat actualitzada correctament.");
        return "redirect:/activitatformacio/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteActivitat(@PathVariable int id, HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        activitatFormacioDao.deleteActivitatFormacio(id);
        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Activitat eliminada.");
        return "redirect:/activitatformacio/list";
    }

    // ==================== VISTA USUARI (Inscripcions) ====================

    @GetMapping("/usuari/list")
    public String listActivitatsUsuari(HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariSession(session);
        if (usuari == null) {
            session.setAttribute("nextUrl", "/activitatformacio/usuari/list");
            return "redirect:/login";
        }

        List<ActivitatFormacio> activitats = activitatFormacioDao.getActivitatsFormacio();

        // Mapa d'inscrits i estat d'inscripció de l'usuari
        Map<Integer, Integer> inscritsMap = new HashMap<>();
        Map<Integer, Boolean> inscritMap = new HashMap<>();
        for (ActivitatFormacio a : activitats) {
            inscritsMap.put(a.getIdActivitat(), assistenciaFormacioDao.countInscrits(a.getIdActivitat()));
            inscritMap.put(a.getIdActivitat(), assistenciaFormacioDao.existsInscripcio(a.getIdActivitat(), usuari.getIdUsuari()));
        }

        model.addAttribute("activitats", activitats);
        model.addAttribute("inscritsMap", inscritsMap);
        model.addAttribute("inscritMap", inscritMap);
        model.addAttribute("usuariLogat", usuari);
        return "activitatFormacio/usuari-list";
    }

    @PostMapping("/usuari/inscriure/{idActivitat}")
    public String inscriureActivitat(@PathVariable int idActivitat, HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        UsuariOVI usuari = getUsuariSession(session);
        if (usuari == null) return "redirect:/login";

        ActivitatFormacio activitat = activitatFormacioDao.getActivitatFormacio(idActivitat);
        if (activitat == null) {
            throw new SgoviException("Activitat no trobada", "Error");
        }

        // Comprovar duplicat
        if (assistenciaFormacioDao.existsInscripcio(idActivitat, usuari.getIdUsuari())) {
            redirectAttributes.addFlashAttribute("missatgeErrorFlash", "Ja esteu inscrit a aquesta activitat.");
            return "redirect:/activitatformacio/usuari/list";
        }

        // Comprovar aforament
        int inscrits = assistenciaFormacioDao.countInscrits(idActivitat);
        if (inscrits >= activitat.getAforament()) {
            redirectAttributes.addFlashAttribute("missatgeErrorFlash", "No queden places disponibles per a aquesta activitat.");
            return "redirect:/activitatformacio/usuari/list";
        }

        AssistenciaFormacio inscripcio = new AssistenciaFormacio();
        inscripcio.setIdAssistencia(assistenciaFormacioDao.getNextId());
        inscripcio.setIdActivitat(idActivitat);
        inscripcio.setIdUsuari(usuari.getIdUsuari());
        inscripcio.setAssisteix(false);
        inscripcio.setCertificatEmes(false);

        assistenciaFormacioDao.addAssistenciaFormacio(inscripcio);
        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Inscripció a '" + activitat.getTitol() + "' realitzada correctament.");
        return "redirect:/activitatformacio/usuari/list";
    }

    @PostMapping("/usuari/desinscriure/{idActivitat}")
    public String desinscriureActivitat(@PathVariable int idActivitat, HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        UsuariOVI usuari = getUsuariSession(session);
        if (usuari == null) return "redirect:/login";

        // Trobar la inscripció i eliminar-la
        List<AssistenciaFormacio> inscripcions = assistenciaFormacioDao.getAssistenciesByActivitat(idActivitat);
        for (AssistenciaFormacio af : inscripcions) {
            if (af.getIdUsuari() != null && af.getIdUsuari() == usuari.getIdUsuari()) {
                assistenciaFormacioDao.deleteAssistenciaFormacio(af.getIdAssistencia());
                break;
            }
        }

        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Inscripció cancel·lada correctament.");
        return "redirect:/activitatformacio/usuari/list";
    }
}
