package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.*;
import es.uji.ei1027.sgovi.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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
    private ComunicacioUsuariOVIPAPDao comunicacioDao;
    private RegistreContracteDao registreContracteDao;

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

    @Autowired
    public void setComunicacioDao(ComunicacioUsuariOVIPAPDao comunicacioDao) {
        this.comunicacioDao = comunicacioDao;
    }

    @Autowired
    public void setRegistreContracteDao(RegistreContracteDao registreContracteDao) {
        this.registreContracteDao = registreContracteDao;
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

    // Genera una comunicació simulada (email simulat)
    private void generarComunicacio(String destinatari, String assumpte, String missatge, String tipus) {
        ComunicacioUsuariOVIPAP c = new ComunicacioUsuariOVIPAP();
        c.setIdComunicacio(comunicacioDao.getNextId());
        c.setDestinatari(destinatari);
        c.setAssumpte(assumpte);
        c.setDataHora(LocalDateTime.now());
        c.setEmissor("tecnic@ovi.es");
        c.setMissatge(missatge);
        c.setTipusComunicacio(tipus);
        comunicacioDao.addComunicacio(c);
    }

    // GET /tecnic/solicituds - panell principal del tècnic
    @RequestMapping(value = "/solicituds", method = RequestMethod.GET)
    public String llistarSolicituds(@RequestParam(value = "cerca", required = false) String cerca,
                                    @RequestParam(value = "estatFiltre", required = false) String estatFiltre,
                                    HttpSession session, Model model) {
        UsuariOVI tecnic = getTecnicSession(session);
        if (tecnic == null) {
            session.setAttribute("nextUrl", "/tecnic/solicituds");
            return "redirect:/login";
        }

        Map<Integer, String> mapNoms = getMapaNomsUsuaris();
        String cercaLower = (cerca != null) ? cerca.toLowerCase() : null;

        List<APRequest> pendents = apRequestDao.getAPRequestsPendents().stream().filter(s -> {
            if (cercaLower != null && !cercaLower.trim().isEmpty()) {
                String reqId = "req-2026-" + String.format("%04d", s.getIdRequest());
                String nomU = mapNoms.getOrDefault(s.getIdUsuari(), "").toLowerCase();
                if (!reqId.contains(cercaLower) && !nomU.contains(cercaLower)) return false;
            }
            return true;
        }).sorted((a, b) -> Integer.compare(b.getIdRequest(), a.getIdRequest())).toList();

        List<APRequest> totes = apRequestDao.getAPRequests().stream().filter(s -> {
            if (cercaLower != null && !cercaLower.trim().isEmpty()) {
                String reqId = "req-2026-" + String.format("%04d", s.getIdRequest());
                String nomU = mapNoms.getOrDefault(s.getIdUsuari(), "").toLowerCase();
                if (!reqId.contains(cercaLower) && !nomU.contains(cercaLower)) return false;
            }
            if (estatFiltre != null && !estatFiltre.trim().isEmpty()) {
                if (!s.getEstat().equalsIgnoreCase(estatFiltre)) return false;
            }
            return true;
        }).sorted((a, b) -> Integer.compare(b.getIdRequest(), a.getIdRequest())).toList();

        model.addAttribute("pendents", pendents);
        model.addAttribute("totes", totes);
        model.addAttribute("nomsUsuaris", mapNoms);
        model.addAttribute("cerca", cerca);
        model.addAttribute("estatFiltre", estatFiltre);
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

        UsuariOVI solicitant = usuariOVIDao.getUsuariOVI(solicitud.getIdUsuari());
        String nomSolicitant = solicitant != null
                ? solicitant.getNom() + " " + solicitant.getCognoms()
                : "Usuari #" + solicitud.getIdUsuari();

        // Buscar contractes associats a aquesta sol·licitud
        List<RegistreContracte> contractes = registreContracteDao.getRegistresContracte().stream()
                .filter(c -> c.getIdRequest() != null && c.getIdRequest() == id)
                .toList();
        
        for (RegistreContracte c : contractes) {
            AssistentPersonal ap = assistentPersonalDao.getAssistentPersonal(c.getIdAssistent());
            if (ap != null) {
                c.setNomAssistentComplet(ap.getNom() + " " + ap.getCognoms());
            } else {
                c.setNomAssistentComplet("Assistent #" + c.getIdAssistent());
            }
        }

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("nomSolicitant", nomSolicitant);
        model.addAttribute("contractes", contractes);
        
        List<Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(id);
        for (Seleccion s : seleccions) {
            AssistentPersonal ap = assistentPersonalDao.getAssistentPersonal(s.getIdAssistent());
            if (ap != null) {
                s.setNomAssistentComplet(ap.getNom() + " " + ap.getCognoms());
            } else {
                s.setNomAssistentComplet("Assistent #" + s.getIdAssistent());
            }
        }
        model.addAttribute("seleccions", seleccions);
        
        model.addAttribute("usuariLogat", tecnic);
        return "tecnic/detall-solicitud";
    }

    // POST /tecnic/solicitud/{id}/aprovar
    @RequestMapping(value = "/solicitud/{id}/aprovar", method = RequestMethod.POST)
    public String aprovarSolicitud(@PathVariable int id, HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getAPRequest(id);
        apRequestDao.updateEstat(id, "aprovada");

        // Generar comunicació simulada (email)
        if (solicitud != null) {
            UsuariOVI usuari = usuariOVIDao.getUsuariOVI(solicitud.getIdUsuari());
            if (usuari != null) {
                generarComunicacio(
                        usuari.getEmail(),
                        "Sol·licitud d'assistència aprovada - REQ-2026-" + String.format("%04d", id),
                        "Estimat/ada " + usuari.getNom() + ",\n\n" +
                                "La seua sol·licitud d'assistència personal (REQ-2026-" + String.format("%04d", id) +
                                ") ha estat APROVADA.\n\n" +
                                "Ja pot accedir al sistema per consultar la llista de candidats " +
                                solicitud.getTipusAssistencia() + " disponibles.\n\n" +
                                "Atentament,\nTècnic OVI",
                        "acceptacio_solicitud"
                );
            }
        }

        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Sol·licitud #" + id + " aprovada correctament. L'usuari ja pot consultar candidats.");
        return "redirect:/tecnic/solicitud/" + id;
    }

    // GET /tecnic/solicitud/{idRequest}/candidats - Llista candidats per proposar
    @RequestMapping(value = "/solicitud/{idRequest}/candidats", method = RequestMethod.GET)
    public String veureCandidats(@PathVariable int idRequest, 
                                 @RequestParam(value = "cerca", required = false) String cerca,
                                 HttpSession session, Model model) {
        UsuariOVI tecnic = getTecnicSession(session);
        if (tecnic == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getAPRequest(idRequest);
        if (solicitud == null) throw new SgoviException("Sol·licitud no trobada", "Error");

        UsuariOVI solicitant = usuariOVIDao.getUsuariOVI(solicitud.getIdUsuari());
        String nomSolicitant = solicitant != null
                ? solicitant.getNom() + " " + solicitant.getCognoms()
                : "Usuari #" + solicitud.getIdUsuari();

        List<AssistentPersonal> tots = assistentPersonalDao.getAssistentsAcceptatsByTipus(solicitud.getTipusAssistencia());
        List<AssistentPersonal> candidats = new java.util.ArrayList<>();
        String cercaLower = (cerca != null) ? cerca.toLowerCase() : null;

        // Extraure paraules clau de les necessitats de la sol·licitud (mes de 3 lletres)
        String necessitats = (solicitud.getDescripcioNecessitats() != null) ? solicitud.getDescripcioNecessitats().toLowerCase() : "";
        String[] paraulesClau = necessitats.split("\\W+");
        List<String> keywords = new java.util.ArrayList<>();
        for (String p : paraulesClau) {
            if (p.length() > 3) keywords.add(p);
        }

        for (AssistentPersonal a : tots) {
            String exp = (a.getExperiencia() != null) ? a.getExperiencia().toLowerCase() : "";
            String form = (a.getFormacio() != null) ? a.getFormacio().toLowerCase() : "";
            String disp = (a.getDisponibilitat() != null) ? a.getDisponibilitat().toLowerCase() : "";
            
            // Càlcul d'afinitat
            int afinitat = 0;
            String textAssistent = exp + " " + form + " " + disp;
            for (String kw : keywords) {
                if (textAssistent.contains(kw)) {
                    afinitat++;
                }
            }
            a.setAfinitat(afinitat);
            a.setRecomanat(afinitat > 0);

            // Filtratge per cerca manual
            if (cercaLower != null && !cercaLower.trim().isEmpty()) {
                String nomCompleto = (a.getNom() + " " + a.getCognoms()).toLowerCase();
                if (!nomCompleto.contains(cercaLower) && 
                    !exp.contains(cercaLower) && 
                    !form.contains(cercaLower) && 
                    !disp.contains(cercaLower)) {
                    continue; // Skip if no match
                }
            }
            candidats.add(a);
        }

        // Ordenar per afinitat (més recomanats primer)
        candidats.sort((a, b) -> Integer.compare(b.getAfinitat(), a.getAfinitat()));

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("nomSolicitant", nomSolicitant);
        model.addAttribute("candidats", candidats);
        model.addAttribute("cerca", cerca);
        model.addAttribute("usuariLogat", tecnic);
        return "tecnic/candidats";
    }

    // POST /tecnic/solicitud/{idRequest}/seleccionar/{idAssistent} - El tècnic proposa un assistent
    @RequestMapping(value = "/solicitud/{idRequest}/seleccionar/{idAssistent}", method = RequestMethod.POST)
    public String seleccionarAssistent(@PathVariable int idRequest, @PathVariable int idAssistent,
                                       HttpSession session, RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";

        // Comprovar si ja existeix una selecció similar
        List<Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(idRequest);
        boolean jaProposat = seleccions.stream().anyMatch(s -> s.getIdAssistent() == idAssistent);
        if (jaProposat) {
            redirectAttributes.addFlashAttribute("missatgeErrorFlash", "Aquest assistent ja ha estat proposat per a la sol·licitud.");
            return "redirect:/tecnic/solicitud/" + idRequest + "/candidats";
        }

        Seleccion seleccion = new Seleccion();
        int nouId = seleccionDao.getSeleccions().stream().mapToInt(Seleccion::getIdSeleccion).max().orElse(0) + 1;
        seleccion.setIdSeleccion(nouId);
        seleccion.setIdRequest(idRequest);
        seleccion.setIdAssistent(idAssistent);
        seleccion.setDataProposta(java.time.LocalDate.now());
        seleccion.setEstat("pendent_ambdos");

        seleccionDao.addSeleccion(seleccion);

        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Assistent proposat correctament. S'ha creat una selecció pendent d'aprovació per les dues parts.");
        return "redirect:/tecnic/solicitud/" + idRequest;
    }

    // POST /tecnic/solicitud/{id}/rebutjar
    @RequestMapping(value = "/solicitud/{id}/rebutjar", method = RequestMethod.POST)
    public String rebutjarSolicitud(@PathVariable int id,
                                    @RequestParam(value = "motiu", required = false) String motiu,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (getTecnicSession(session) == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getAPRequest(id);
        apRequestDao.updateEstat(id, "rebutjada");

        // Cancelar qualsevol selecció pendent d'aquesta sol·licitud
        List<Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(id);
        for (Seleccion s : seleccions) {
            if (!"Rebutjada".equals(s.getEstat())) {
                s.setEstat("Rebutjada");
                seleccionDao.updateSeleccion(s);
            }
        }

        // Generar comunicació simulada (email)
        if (solicitud != null) {
            UsuariOVI usuari = usuariOVIDao.getUsuariOVI(solicitud.getIdUsuari());
            if (usuari != null) {
                String motiuText = (motiu != null && !motiu.trim().isEmpty()) ? motiu : "No s'ha especificat cap motiu.";
                generarComunicacio(
                        usuari.getEmail(),
                        "Sol·licitud d'assistència rebutjada - REQ-2026-" + String.format("%04d", id),
                        "Estimat/ada " + usuari.getNom() + ",\n\n" +
                                "La seua sol·licitud d'assistència personal (REQ-2026-" + String.format("%04d", id) +
                                ") ha estat REBUTJADA.\n\n" +
                                "Motiu: " + motiuText + "\n\n" +
                                "Atentament,\nTècnic OVI",
                        "rebuig_solicitud"
                );
            }
        }

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
}
