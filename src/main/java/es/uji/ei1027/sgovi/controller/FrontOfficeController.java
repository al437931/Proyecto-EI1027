package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.APRequestDao;
import es.uji.ei1027.sgovi.dao.AssistentPersonalDao;
import es.uji.ei1027.sgovi.dao.RegistreContracteDao;
import es.uji.ei1027.sgovi.model.APRequest;
import es.uji.ei1027.sgovi.model.AssistentPersonal;
import es.uji.ei1027.sgovi.model.RegistreContracte;
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
import java.util.regex.Pattern;

@Controller
@RequestMapping("/usuari")
public class FrontOfficeController {

    private APRequestDao apRequestDao;
    private es.uji.ei1027.sgovi.dao.SeleccionDao seleccionDao;
    private AssistentPersonalDao assistentPersonalDao;
    private es.uji.ei1027.sgovi.dao.UsuariOVIDao usuariOVIDao;
    private RegistreContracteDao registreContracteDao;

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
    public void setAssistentPersonalDao(AssistentPersonalDao assistentPersonalDao) {
        this.assistentPersonalDao = assistentPersonalDao;
    }

    @Autowired
    public void setRegistreContracteDao(RegistreContracteDao registreContracteDao) {
        this.registreContracteDao = registreContracteDao;
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
        
        for (APRequest req : solicituds) {
            List<RegistreContracte> contractes = registreContracteDao.getContractesByRequest(req.getIdRequest());
            if (!contractes.isEmpty()) {
                AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(contractes.get(0).getIdAssistent());
                if (assistent != null) {
                    req.setNomAssistentAssignat(assistent.getNom() + " " + (assistent.getCognoms() != null ? assistent.getCognoms() : ""));
                }
            } else {
                List<es.uji.ei1027.sgovi.model.Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(req.getIdRequest());
                String nomAssistent = null;
                for (es.uji.ei1027.sgovi.model.Seleccion s : seleccions) {
                    if ("Acceptada".equals(s.getEstat())) {
                        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(s.getIdAssistent());
                        if (assistent != null) {
                            nomAssistent = assistent.getNom() + " " + (assistent.getCognoms() != null ? assistent.getCognoms() : "");
                            break;
                        }
                    } else if ("Pendent Usuari".equals(s.getEstat()) || "Pendent Assistent".equals(s.getEstat()) || "pendent_ambdos".equals(s.getEstat())) {
                        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(s.getIdAssistent());
                        if (assistent != null) {
                            nomAssistent = assistent.getNom() + " " + (assistent.getCognoms() != null ? assistent.getCognoms() : "") + " (Pendent)";
                        }
                    }
                }
                if (nomAssistent != null) {
                    req.setNomAssistentAssignat(nomAssistent);
                } else {
                    req.setNomAssistentAssignat("-");
                }
            }
        }
        
        model.addAttribute("solicituds", solicituds);
        model.addAttribute("usuariLogat", usuari);
        return "usuari/solicituds";
    }

    // GET /usuari/historial - Veure l'historial de contractes de l'usuari
    @RequestMapping(value = "/historial", method = RequestMethod.GET)
    public String historial(HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) {
            session.setAttribute("nextUrl", "/usuari/historial");
            return "redirect:/login";
        }
        
        List<RegistreContracte> contractes = registreContracteDao.getContractesByUsuari(usuari.getIdUsuari());
        for (RegistreContracte contracte : contractes) {
            if (contracte.getIdAssistent() != null && contracte.getIdAssistent() > 0) {
                AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(contracte.getIdAssistent());
                if (assistent != null) {
                    contracte.setNomAssistentComplet(assistent.getNom() + " " + (assistent.getCognoms() != null ? assistent.getCognoms() : ""));
                } else {
                    contracte.setNomAssistentComplet("Assistent Desconegut");
                }
            } else {
                contracte.setNomAssistentComplet("-");
            }
        }
        
        model.addAttribute("contractes", contractes);
        model.addAttribute("usuariLogat", usuari);
        return "usuari/historial";
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

    // POST /usuari/perfil - Actualitzar dades del perfil amb validació
    @RequestMapping(value = "/perfil", method = RequestMethod.POST)
    public String actualitzarPerfil(@ModelAttribute("usuariOVI") UsuariOVI usuariOVI,
                                    BindingResult bindingResult,
                                    HttpSession session, Model model,
                                    RedirectAttributes redirectAttributes) {
        UsuariOVI usuariLogat = getUsuariOSession(session);
        if (usuariLogat == null) return "redirect:/login";

        // Validacions bàsiques del perfil
        Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s'-]+$");
        Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9}$");

        if (usuariOVI.getNom() == null || usuariOVI.getNom().trim().isEmpty()) {
            bindingResult.rejectValue("nom", "obligatori", "El nom és obligatori.");
        } else if (!NAME_PATTERN.matcher(usuariOVI.getNom().trim()).matches()) {
            bindingResult.rejectValue("nom", "format.invalid", "El nom només pot contindre lletres.");
        }

        if (usuariOVI.getCognoms() == null || usuariOVI.getCognoms().trim().isEmpty()) {
            bindingResult.rejectValue("cognoms", "obligatori", "Els cognoms són obligatoris.");
        } else if (!NAME_PATTERN.matcher(usuariOVI.getCognoms().trim()).matches()) {
            bindingResult.rejectValue("cognoms", "format.invalid", "Els cognoms només poden contindre lletres.");
        }

        if (usuariOVI.getTelefon() != null && !usuariOVI.getTelefon().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(usuariOVI.getTelefon().trim()).matches()) {
                bindingResult.rejectValue("telefon", "format.invalid", "El telèfon ha de tindre exactament 9 dígits.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuariLogat", usuariLogat);
            return "usuari/perfil";
        }

        UsuariOVI existent = usuariOVIDao.getUsuariOVI(usuariLogat.getIdUsuari());
        existent.setNom(usuariOVI.getNom());
        existent.setCognoms(usuariOVI.getCognoms());
        existent.setTelefon(usuariOVI.getTelefon());
        existent.setAdreca(usuariOVI.getAdreca());

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

        if ("tancada".equals(solicitud.getEstat())) {
            List<RegistreContracte> contractes = registreContracteDao.getContractesByRequest(id);
            for (RegistreContracte c : contractes) {
                AssistentPersonal ap = assistentPersonalDao.getAssistentPersonal(c.getIdAssistent());
                if (ap != null) {
                    c.setNomAssistentComplet(ap.getNom() + " " + ap.getCognoms());
                } else {
                    c.setNomAssistentComplet("Assistent #" + c.getIdAssistent());
                }
            }
            model.addAttribute("contractes", contractes);
        }
        
        List<es.uji.ei1027.sgovi.model.Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(id);
        for (es.uji.ei1027.sgovi.model.Seleccion s : seleccions) {
            AssistentPersonal ap = assistentPersonalDao.getAssistentPersonal(s.getIdAssistent());
            if (ap != null) {
                s.setNomAssistentComplet(ap.getNom() + " " + ap.getCognoms());
            } else {
                s.setNomAssistentComplet("Assistent #" + s.getIdAssistent());
            }
        }
        model.addAttribute("seleccions", seleccions);

        return "usuari/detall-solicitud";
    }

    // GET /usuari/solicitud/{id}/candidats - L'USUARI veu la llista de candidats PAP/PATI
    @RequestMapping(value = "/solicitud/{id}/candidats", method = RequestMethod.GET)
    public String veureCandidats(@PathVariable int id, HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getAPRequest(id);
        if (solicitud == null || solicitud.getIdUsuari() != usuari.getIdUsuari()) {
            throw new SgoviException("No teniu permís per accedir a aquesta sol·licitud", "Accés no autoritzat");
        }

        if (!"aprovada".equals(solicitud.getEstat())) {
            throw new SgoviException("Aquesta sol·licitud no està aprovada, no es poden veure candidats.", "Error");
        }

        // Filtrar candidats per tipus d'assistència de la sol·licitud
        List<AssistentPersonal> candidats = assistentPersonalDao.getAssistentsAcceptatsByTipus(solicitud.getTipusAssistencia());

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("candidats", candidats);
        model.addAttribute("usuariLogat", usuari);
        
        // Obtenir seleccions actuals per a no tornar a proposar el mateix
        List<es.uji.ei1027.sgovi.model.Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(id);
        model.addAttribute("seleccions", seleccions);
        
        return "usuari/candidats";
    }

    // POST /usuari/solicitud/{idRequest}/proposar/{idAssistent} - L'usuari proposa un assistent
    @PostMapping("/solicitud/{idRequest}/proposar/{idAssistent}")
    public String proposarAssistent(@PathVariable int idRequest, @PathVariable int idAssistent, HttpSession session, RedirectAttributes redirectAttributes) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getAPRequest(idRequest);
        if (solicitud == null || solicitud.getIdUsuari() != usuari.getIdUsuari()) {
            throw new es.uji.ei1027.sgovi.controller.SgoviException("No teniu permís", "Accés no autoritzat");
        }

        if (!"aprovada".equals(solicitud.getEstat())) {
            throw new es.uji.ei1027.sgovi.controller.SgoviException("Aquesta sol·licitud no està aprovada.", "Error");
        }

        // Comprovar que no s'ha proposat ja
        List<es.uji.ei1027.sgovi.model.Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(idRequest);
        boolean jaProposat = seleccions.stream().anyMatch(s -> s.getIdAssistent() == idAssistent);
        if (jaProposat) {
            redirectAttributes.addFlashAttribute("missatgeErrorFlash", "Ja heu proposat aquest assistent.");
            return "redirect:/usuari/solicitud/" + idRequest + "/candidats";
        }

        // Crear selecció "Pendent Assistent"
        es.uji.ei1027.sgovi.model.Seleccion seleccion = new es.uji.ei1027.sgovi.model.Seleccion();
        int nouId = seleccionDao.getSeleccions().stream().mapToInt(es.uji.ei1027.sgovi.model.Seleccion::getIdSeleccion).max().orElse(0) + 1;
        seleccion.setIdSeleccion(nouId);
        seleccion.setIdRequest(idRequest);
        seleccion.setIdAssistent(idAssistent);
        seleccion.setDataProposta(LocalDate.now());
        seleccion.setEstat("Pendent Assistent");

        seleccionDao.addSeleccion(seleccion);

        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Heu proposat l'assistent correctament. Resta a l'espera que l'assistent accepte la proposta.");
        return "redirect:/usuari/solicitud/" + idRequest + "/candidats";
    }

    // POST /usuari/seleccion/{idSeleccion}/acceptar - L'usuari accepta la proposta del tècnic
    @PostMapping("/seleccion/{idSeleccion}/acceptar")
    public String acceptarSeleccionUsuari(@PathVariable int idSeleccion, HttpSession session, RedirectAttributes redirectAttributes) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) return "redirect:/login";

        es.uji.ei1027.sgovi.model.Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion != null) {
            if ("Pendent Usuari".equals(seleccion.getEstat())) {
                seleccion.setEstat("Acceptada");
                seleccionDao.updateSeleccion(seleccion);
                redirectAttributes.addFlashAttribute("missatgeExitFlash", "Heu acceptat la proposta de l'assistent. El match és complet! Ja podeu registrar el contracte.");
                return "redirect:/usuari/solicitud/" + seleccion.getIdRequest();
            } else if ("pendent_ambdos".equals(seleccion.getEstat())) {
                seleccion.setEstat("Pendent Assistent");
                seleccionDao.updateSeleccion(seleccion);
                redirectAttributes.addFlashAttribute("missatgeExitFlash", "Heu acceptat la proposta del tècnic. Estem a l'espera que l'assistent també l'accepte.");
                return "redirect:/usuari/solicitud/" + seleccion.getIdRequest();
            }
        }
        return "redirect:/usuari/solicituds";
    }

    // GET /usuari/solicitud/{idRequest}/registrar-contracte - Formulari per registrar contracte
    @RequestMapping(value = "/solicitud/{idRequest}/registrar-contracte", method = RequestMethod.GET)
    public String registrarContracteForm(@PathVariable int idRequest, HttpSession session, Model model) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getAPRequest(idRequest);
        if (solicitud == null || solicitud.getIdUsuari() != usuari.getIdUsuari()) {
            throw new SgoviException("No teniu permís", "Accés no autoritzat");
        }

        // Candidats compatibles per al dropdown
        List<AssistentPersonal> candidats = assistentPersonalDao.getAssistentsAcceptatsByTipus(solicitud.getTipusAssistencia());

        RegistreContracte contracte = new RegistreContracte();
        contracte.setIdRequest(idRequest);

        model.addAttribute("registreContracte", contracte);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("candidats", candidats);
        model.addAttribute("usuariLogat", usuari);
        return "usuari/registrar-contracte";
    }

    // POST /usuari/solicitud/{idRequest}/registrar-contracte - Guardar contracte
    @RequestMapping(value = "/solicitud/{idRequest}/registrar-contracte", method = RequestMethod.POST)
    public String registrarContracte(@PathVariable int idRequest,
                                     @ModelAttribute("registreContracte") RegistreContracte contracte,
                                     BindingResult bindingResult,
                                     HttpSession session, Model model,
                                     RedirectAttributes redirectAttributes) {
        UsuariOVI usuari = getUsuariOSession(session);
        if (usuari == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getAPRequest(idRequest);
        if (solicitud == null || solicitud.getIdUsuari() != usuari.getIdUsuari()) {
            throw new SgoviException("No teniu permís", "Accés no autoritzat");
        }

        // Validacions
        if (contracte.getIdAssistent() == null) {
            bindingResult.rejectValue("idAssistent", "obligatori", "Cal seleccionar un assistent.");
        }
        if (contracte.getDataInici() == null) {
            bindingResult.rejectValue("dataInici", "obligatori", "La data d'inici és obligatòria.");
        }
        if (contracte.getDataFi() != null && contracte.getDataInici() != null
                && contracte.getDataFi().isBefore(contracte.getDataInici())) {
            bindingResult.rejectValue("dataFi", "dates.invalid", "La data de fi ha de ser posterior a la d'inici.");
        }

        if (bindingResult.hasErrors()) {
            List<AssistentPersonal> candidats = assistentPersonalDao.getAssistentsAcceptatsByTipus(solicitud.getTipusAssistencia());
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("candidats", candidats);
            model.addAttribute("usuariLogat", usuari);
            return "usuari/registrar-contracte";
        }

        // Generar ID
        List<RegistreContracte> tots = registreContracteDao.getRegistresContracte();
        int nouId = tots.stream().mapToInt(RegistreContracte::getIdContracte).max().orElse(0) + 1;
        contracte.setIdContracte(nouId);
        contracte.setIdRequest(idRequest);

        registreContracteDao.addRegistreContracte(contracte);

        // Tancar la sol·licitud
        apRequestDao.updateEstat(idRequest, "tancada");

        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Contracte registrat correctament. La sol·licitud ha estat tancada.");
        return "redirect:/usuari/solicitud/" + idRequest;
    }
}
