package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.APRequestDao;
import es.uji.ei1027.sgovi.dao.AssistentPersonalDao;
import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.dao.UsuariOVIDao;
import es.uji.ei1027.sgovi.model.APRequest;
import es.uji.ei1027.sgovi.model.AssistentPersonal;
import es.uji.ei1027.sgovi.model.Seleccion;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tecnic")
public class BackOfficeController {

    private APRequestDao apRequestDao;
    private AssistentPersonalDao assistentPersonalDao;
    private SeleccionDao seleccionDao;
    private UsuariOVIDao usuariOVIDao;

    @Autowired
    public void setAPRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @Autowired
    public void setAssistentPersonalDao(AssistentPersonalDao assistentPersonalDao) {
        this.assistentPersonalDao = assistentPersonalDao;
    }

    @Autowired
    public void setSeleccionDao(SeleccionDao seleccionDao) {
        this.seleccionDao = seleccionDao;
    }

    @Autowired
    public void setUsuariOVIDao(UsuariOVIDao usuariOVIDao) {
        this.usuariOVIDao = usuariOVIDao;
    }

    // Comprova que la sessió és de tècnic
    private UsuariOVI getTecnicSession(HttpSession session) {
        UsuariOVI u = (UsuariOVI) session.getAttribute("usuariLogat");
        if (u == null || !"tecnic".equals(u.getRol())) return null;
        return u;
    }

    // Crea un mapa idUsuari -> "Nom Cognoms" per mostrar noms en les taules
    private Map<Integer, String> getMapaNomsUsuaris() {
        Map<Integer, String> mapa = new HashMap<>();
        for (UsuariOVI u : usuariOVIDao.getUsuariOVIs()) {
            mapa.put(u.getIdUsuari(), u.getNom() + " " + u.getCognoms());
        }
        return mapa;
    }

    // GET /tecnic/solicituds - panell principal del tècnic
    @RequestMapping(value = "/solicituds", method = RequestMethod.GET)
    public String llistarSolicituds(HttpSession session, Model model) {
        UsuariOVI tecnic = getTecnicSession(session);
        if (tecnic == null) {
            session.setAttribute("nextUrl", "/tecnic/solicituds");
            return "redirect:/login";
        }
        model.addAttribute("pendents", apRequestDao.getAPRequestsPendents());
        model.addAttribute("totes", apRequestDao.getAPRequests());
        model.addAttribute("nomsUsuaris", getMapaNomsUsuaris());
        model.addAttribute("usuariLogat", tecnic);
        return "tecnic/solicituds";
    }

    // GET /tecnic/solicitud/{id} - detall per al tècnic
    @RequestMapping(value = "/solicitud/{id}", method = RequestMethod.GET)
    public String detallSolicitud(@PathVariable int id, HttpSession session, Model model) {
        UsuariOVI tecnic = getTecnicSession(session);
        if (tecnic == null) {
            session.setAttribute("nextUrl", "/tecnic/solicitud/" + id);
            return "redirect:/login";
        }
        APRequest solicitud = apRequestDao.getAPRequest(id);
        if (solicitud == null)
            throw new SgoviException("Sol·licitud no trobada", "Error");

        // Obtenir el nom de l'usuari sol·licitant
        UsuariOVI solicitant = usuariOVIDao.getUsuariOVI(solicitud.getIdUsuari());
        String nomSolicitant = solicitant != null
                ? solicitant.getNom() + " " + solicitant.getCognoms()
                : "Usuari #" + solicitud.getIdUsuari();

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("nomSolicitant", nomSolicitant);
        model.addAttribute("usuariLogat", tecnic);
        return "tecnic/detall-solicitud";
    }

    // POST /tecnic/solicitud/{id}/aprovar
    @RequestMapping(value = "/solicitud/{id}/aprovar", method = RequestMethod.POST)
    public String aprovarSolicitud(@PathVariable int id, HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        apRequestDao.updateEstat(id, "aprovada");
        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Sol·licitud #" + id + " aprovada correctament.");
        return "redirect:/tecnic/solicitud/" + id + "/candidats";
    }

    // POST /tecnic/solicitud/{id}/rebutjar
    @RequestMapping(value = "/solicitud/{id}/rebutjar", method = RequestMethod.POST)
    public String rebutjarSolicitud(@PathVariable int id, HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        apRequestDao.updateEstat(id, "rebutjada");
        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Sol·licitud #" + id + " rebutjada.");
        return "redirect:/tecnic/solicituds";
    }

    // POST /tecnic/solicitud/{id}/revertir
    @RequestMapping(value = "/solicitud/{id}/revertir", method = RequestMethod.POST)
    public String revertirSolicitud(@PathVariable int id, HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        apRequestDao.updateEstat(id, "en revisio");
        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "S'ha desfet l'aprovació de la sol·licitud REQ-2026-" + String.format("%04d", id) + ".");
        return "redirect:/tecnic/solicitud/" + id;
    }

    // GET /tecnic/solicitud/{id}/candidats - proposar candidats
    @RequestMapping(value = "/solicitud/{id}/candidats", method = RequestMethod.GET)
    public String proposarCandidats(@PathVariable int id, HttpSession session, Model model) {
        UsuariOVI tecnic = getTecnicSession(session);
        if (tecnic == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getAPRequest(id);
        if (solicitud == null)
            throw new SgoviException("Sol·licitud no trobada", "Error");

        List<AssistentPersonal> candidats = assistentPersonalDao.getAssistentsAcceptats();

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("candidats", candidats);
        model.addAttribute("usuariLogat", tecnic);
        return "tecnic/candidats";
    }

    // POST /tecnic/solicitud/{idRequest}/seleccionar/{idAssistent}
    @RequestMapping(value = "/solicitud/{idRequest}/seleccionar/{idAssistent}",
            method = RequestMethod.POST)
    public String seleccionarCandidant(@PathVariable Integer idRequest,
                                       @PathVariable Integer idAssistent,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";

        // Generar ID automàtic
        List<Seleccion> totes = seleccionDao.getSeleccions();
        int nouId = totes.stream().mapToInt(Seleccion::getIdSeleccion).max().orElse(0) + 1;

        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(nouId);
        seleccion.setIdRequest(idRequest);
        seleccion.setIdAssistent(idAssistent);
        seleccion.setDataProposta(LocalDate.now());
        seleccion.setEstat("pendent");

        seleccionDao.addSeleccion(seleccion);
        apRequestDao.updateEstat(idRequest, "tancada");

        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Candidat seleccionat correctament per a la sol·licitud #" + idRequest + ".");
        return "redirect:/tecnic/solicituds";
    }
}
