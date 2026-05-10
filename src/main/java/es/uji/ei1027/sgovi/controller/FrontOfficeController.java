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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/usuari")
public class FrontOfficeController {

    private APRequestDao apRequestDao;

    @Autowired
    public void setAPRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
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
                                   HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) return "redirect:/login";

        // Validació
        APRequestValidator validator = new APRequestValidator();
        validator.validate(apRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuariLogat", usuari);
            return "usuari/nova-solicitud";
        }

        // Generar ID nou
        List<APRequest> totes = apRequestDao.getAPRequests();
        int nouId = totes.stream().mapToInt(APRequest::getIdRequest).max().orElse(0) + 1;

        apRequest.setIdRequest(nouId);
        apRequest.setIdUsuari(usuari.getIdUsuari());
        apRequest.setDataCreacio(LocalDate.now());
        apRequest.setEstat("en revisio");

        apRequestDao.addAPRequest(apRequest);
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
        return "usuari/detall-solicitud";
    }
}
