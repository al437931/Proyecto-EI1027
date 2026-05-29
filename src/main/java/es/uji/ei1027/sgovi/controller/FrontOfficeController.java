package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.APRequestDao;
import es.uji.ei1027.sgovi.model.APRequest;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/usuari")
public class FrontOfficeController {

    private APRequestDao apRequestDao;
    private es.uji.ei1027.sgovi.dao.SeleccionDao seleccionDao;
    private es.uji.ei1027.sgovi.dao.AssistentPersonalDao assistentPersonalDao;
    private es.uji.ei1027.sgovi.dao.UsuariOVIDao usuariOVIDao;

    @Autowired
    public void setUsuariOVIDao(es.uji.ei1027.sgovi.dao.UsuariOVIDao usuariOVIDao) {
        this.usuariOVIDao = usuariOVIDao;
    }

    @Autowired
    public void setAPRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @Autowired
    public void setSeleccionDao(es.uji.ei1027.sgovi.dao.SeleccionDao seleccionDao) {
        this.seleccionDao = seleccionDao;
    }

    @Autowired
    public void setAssistentPersonalDao(es.uji.ei1027.sgovi.dao.AssistentPersonalDao assistentPersonalDao) {
        this.assistentPersonalDao = assistentPersonalDao;
    }

    // Comprova sessió d'usuari OVI
    private UsuariOVI getUsuariOSession(HttpSession session) {
        UsuariOVI u = (UsuariOVI) session.getAttribute("usuariLogat");
        if (u == null || !"usuari".equals(u.getRol())) return null;
        return u;
    }

    // GET /usuari/solicituds - llista les sol·licituds de l'usuari loguejat
    @RequestMapping(value = "/solicituds", method = RequestMethod.GET)
    public String llistarSolicituds(HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) {
            session.setAttribute("nextUrl", "/usuari/solicituds");
            return "redirect:/login";
        }
        List<APRequest> solicituds = apRequestDao.getAPRequestsByUsuari(usuari.getIdUsuari());
        model.addAttribute("solicituds", solicituds);
        model.addAttribute("usuariLogat", usuari);
        return "usuari/solicituds";
    }

    // GET /usuari/perfil - Veure el perfil del ciutadà
    @RequestMapping(value = "/perfil", method = RequestMethod.GET)
    public String veurePerfil(HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) return "redirect:/login";
        model.addAttribute("usuariOVI", usuariOVIDao.getUsuariOVI(usuari.getIdUsuari()));
        model.addAttribute("usuariLogat", usuari);
        return "usuari/perfil";
    }

    // POST /usuari/perfil - Actualitzar dades del perfil
    @RequestMapping(value = "/perfil", method = RequestMethod.POST)
    public String actualitzarPerfil(@ModelAttribute("usuariOVI") UsuariOVI usuariOVI,
                                    HttpSession session, RedirectAttributes redirectAttributes) {
        UsuariOVI usuariLogat = getUsuariOSession(session);
        if (usuariLogat == null) return "redirect:/login";

        UsuariOVI existent = usuariOVIDao.getUsuariOVI(usuariLogat.getIdUsuari());
        existent.setNom(usuariOVI.getNom());
        existent.setCognoms(usuariOVI.getCognoms());
        existent.setTelefon(usuariOVI.getTelefon());
        existent.setAdreca(usuariOVI.getAdreca());
        // El correu no es pot canviar ací per evitar col·lisions, o si es canvia s'ha de validar

        usuariOVIDao.updateUsuariOVI(existent);
        session.setAttribute("usuariLogat", existent);
        
        redirectAttributes.addFlashAttribute("missatgeExitFlash", "El perfil s'ha actualitzat correctament.");
        return "redirect:/usuari/perfil";
    }

    // GET /usuari/nova-solicitud - formulari nova sol·licitud
    @RequestMapping(value = "/nova-solicitud", method = RequestMethod.GET)
    public String novaSolicitudForm(HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) {
            session.setAttribute("nextUrl", "/usuari/nova-solicitud");
            return "redirect:/login";
        }
        model.addAttribute("apRequest", new APRequest());
        model.addAttribute("usuariLogat", usuari);
        return "usuari/nova-solicitud";
    }

    // POST /usuari/nova-solicitud - guarda nova sol·licitud
    @RequestMapping(value = "/nova-solicitud", method = RequestMethod.POST)
    public String guardarSolicitud(@ModelAttribute("apRequest") APRequest apRequest,
                                   BindingResult bindingResult,
                                   HttpSession session, Model model,
                                   RedirectAttributes redirectAttributes) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) return "redirect:/login";

        // Validació
        APRequestValidator validator = new APRequestValidator();
        validator.validate(apRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuariLogat", usuari);
            return "usuari/nova-solicitud";
        }

        // Generar ID automàtic
        List<APRequest> totes = apRequestDao.getAPRequests();
        int nouId = totes.stream().mapToInt(APRequest::getIdRequest).max().orElse(0) + 1;

        apRequest.setIdRequest(nouId);
        apRequest.setIdUsuari(usuari.getIdUsuari());
        apRequest.setDataCreacio(LocalDate.now());
        apRequest.setEstat("en revisio");

        apRequestDao.addAPRequest(apRequest);

        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Sol·licitud creada correctament. El tècnic la revisarà prompte.");
        return "redirect:/usuari/solicituds";
    }

    // GET /usuari/solicitud/{id} - detall d'una sol·licitud
    @RequestMapping(value = "/solicitud/{id}", method = RequestMethod.GET)
    public String detallSolicitud(@PathVariable int id, HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) {
            session.setAttribute("nextUrl", "/usuari/solicitud/" + id);
            return "redirect:/login";
        }

        APRequest solicitud = apRequestDao.getAPRequest(id);
        if (solicitud == null || solicitud.getIdUsuari() != usuari.getIdUsuari()) {
            throw new SgoviException(
                    "No teniu permís per accedir a aquesta sol·licitud",
                    "Accés no autoritzat");
        }

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("usuariLogat", usuari);

        // Si la sol·licitud està tancada, carreguem la seua selecció i l'assistent proposat
        if ("tancada".equals(solicitud.getEstat())) {
            List<es.uji.ei1027.sgovi.model.Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(id);
            if (!seleccions.isEmpty()) {
                es.uji.ei1027.sgovi.model.Seleccion seleccion = seleccions.get(0);
                model.addAttribute("seleccion", seleccion);
                es.uji.ei1027.sgovi.model.AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(seleccion.getIdAssistent());
                model.addAttribute("assistentProposat", assistent);
            }
        }

        return "usuari/detall-solicitud";
    }

    // POST /usuari/solicitud/{idRequest}/seleccion/{idSeleccion}/acceptar
    @RequestMapping(value = "/solicitud/{idRequest}/seleccion/{idSeleccion}/acceptar", method = RequestMethod.POST)
    public String acceptarCandidat(@PathVariable int idRequest, @PathVariable int idSeleccion,
                                   HttpSession session, RedirectAttributes redirectAttributes) {
        if (getUsuariOSession(session) == null) return "redirect:/login";
        es.uji.ei1027.sgovi.model.Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion != null) {
            seleccion.setEstat("acceptat");
            seleccionDao.updateSeleccion(seleccion);
            redirectAttributes.addFlashAttribute("missatgeExitFlash", "Candidat acceptat correctament.");
        }
        return "redirect:/usuari/solicitud/" + idRequest;
    }

    // POST /usuari/solicitud/{idRequest}/seleccion/{idSeleccion}/rebutjar
    @RequestMapping(value = "/solicitud/{idRequest}/seleccion/{idSeleccion}/rebutjar", method = RequestMethod.POST)
    public String rebutjarCandidat(@PathVariable int idRequest, @PathVariable int idSeleccion,
                                   HttpSession session, RedirectAttributes redirectAttributes) {
        if (getUsuariOSession(session) == null) return "redirect:/login";
        es.uji.ei1027.sgovi.model.Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion != null) {
            seleccion.setEstat("rebutjat");
            seleccionDao.updateSeleccion(seleccion);
            
            // Tornem a obrir la sol·licitud perquè el tècnic propose un altre
            apRequestDao.updateEstat(idRequest, "aprovada");
            redirectAttributes.addFlashAttribute("missatgeExitFlash", "Candidat rebutjat. La sol·licitud tornarà al tècnic per a una nova proposta.");
        }
        return "redirect:/usuari/solicitud/" + idRequest;
    }
}
