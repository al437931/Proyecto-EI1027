package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.APRequestDao;
import es.uji.ei1027.sgovi.dao.AssistentPersonalDao;
import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.model.APRequest;
import es.uji.ei1027.sgovi.model.AssistentPersonal;
import es.uji.ei1027.sgovi.model.Seleccion;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tecnic")
public class BackOfficeController {

    private APRequestDao apRequestDao;
    private AssistentPersonalDao assistentPersonalDao;
    private SeleccionDao seleccionDao;

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

    // Comprova que la sessió és de tècnic
    private UsuariOVI getTecnicSession(HttpSession session) {
        UsuariOVI u = (UsuariOVI) session.getAttribute("usuariLogat");
        if (u == null || !"tecnic".equals(u.getRol())) return null;
        return u;
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

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("usuariLogat", tecnic);
        return "tecnic/detall-solicitud";
    }

    // POST /tecnic/solicitud/{id}/aprovar
    @RequestMapping(value = "/solicitud/{id}/aprovar", method = RequestMethod.POST)
    public String aprovarSolicitud(@PathVariable int id, HttpSession session) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        apRequestDao.updateEstat(id, "aprovada");
        return "redirect:/tecnic/solicitud/" + id + "/candidats";
    }

    // POST /tecnic/solicitud/{id}/rebutjar
    @RequestMapping(value = "/solicitud/{id}/rebutjar", method = RequestMethod.POST)
    public String rebutjarSolicitud(@PathVariable int id, HttpSession session) {
        if (getTecnicSession(session) == null) return "redirect:/login";
        apRequestDao.updateEstat(id, "rebutjada");
        return "redirect:/tecnic/solicituds";
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
    public String seleccionarCandidant(@PathVariable int idRequest,
                                       @PathVariable int idAssistent,
                                       HttpSession session) {
        if (getTecnicSession(session) == null) return "redirect:/login";

        List<Seleccion> totes = seleccionDao.getSeleccions();
        int nouId = totes.stream().mapToInt(Seleccion::getIdSeleccion).max().orElse(0) + 1;

        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(nouId);
        seleccion.setIdRequest(idRequest);
        seleccion.setIdAssistent(idAssistent);
        seleccion.setDataProposta(LocalDate.now());
        seleccion.setEstat("proposat");

        seleccionDao.addSeleccion(seleccion);
        apRequestDao.updateEstat(idRequest, "tancada amb contracte");

        return "redirect:/tecnic/solicituds";
    }
}
