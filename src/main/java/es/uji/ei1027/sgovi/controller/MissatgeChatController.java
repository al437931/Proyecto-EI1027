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
import java.util.*;

/**
 * Controller per al mini-chat entre usuari i assistent,
 * vinculat a una Selecció (proposta de match).
 */
@Controller
@RequestMapping("/missatge")
public class MissatgeChatController {

    @Autowired
    private MissatgeChatDao missatgeChatDao;
    @Autowired
    private SeleccionDao seleccionDao;
    @Autowired
    private APRequestDao apRequestDao;
    @Autowired
    private UsuariOVIDao usuariOVIDao;
    @Autowired
    private AssistentPersonalDao assistentPersonalDao;

    /**
     * GET /missatge/chat/{idSeleccion}
     * Mostra el fil de missatges d'una selecció.
     * Accessible per l'usuari de la sol·licitud i per l'assistent de la selecció.
     */
    @GetMapping("/chat/{idSeleccion}")
    public String chat(@PathVariable int idSeleccion, HttpSession session, Model model) {
        UsuariOVI logat = (UsuariOVI) session.getAttribute("usuariLogat");
        if (logat == null) {
            session.setAttribute("nextUrl", "/missatge/chat/" + idSeleccion);
            return "redirect:/login";
        }

        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null) {
            throw new SgoviException("Selecció no trobada", "Error");
        }

        // Comprovar permisos: l'usuari de la sol·licitud o l'assistent de la selecció
        String rolChat = determinarRolChat(logat, seleccion);
        if (rolChat == null) {
            throw new SgoviException("No teniu permís per accedir a aquest xat.", "Accés no autoritzat");
        }

        // Obtenir dades per a la vista
        APRequest solicitud = apRequestDao.getAPRequest(seleccion.getIdRequest());
        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(seleccion.getIdAssistent());
        UsuariOVI usuariSolicitud = (solicitud != null) ? usuariOVIDao.getUsuariOVI(solicitud.getIdUsuari()) : null;

        List<MissatgeChat> missatges = missatgeChatDao.getMissatgesBySeleccion(idSeleccion);

        model.addAttribute("seleccion", seleccion);
        model.addAttribute("missatges", missatges);
        model.addAttribute("rolChat", rolChat);
        model.addAttribute("usuariLogat", logat);
        model.addAttribute("nomUsuari", usuariSolicitud != null ? usuariSolicitud.getNom() + " " + usuariSolicitud.getCognoms() : "Usuari");
        model.addAttribute("nomAssistent", assistent != null ? assistent.getNom() + " " + assistent.getCognoms() : "Assistent");
        model.addAttribute("nouMissatge", new MissatgeChat());

        return "missatge/chat";
    }

    /**
     * POST /missatge/enviar/{idSeleccion}
     * Envia un missatge al fil de la selecció.
     */
    @PostMapping("/enviar/{idSeleccion}")
    public String enviarMissatge(@PathVariable int idSeleccion,
                                  @RequestParam("missatge") String missatgeText,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        UsuariOVI logat = (UsuariOVI) session.getAttribute("usuariLogat");
        if (logat == null) return "redirect:/login";

        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null) {
            throw new SgoviException("Selecció no trobada", "Error");
        }

        String rolChat = determinarRolChat(logat, seleccion);
        if (rolChat == null) {
            throw new SgoviException("No teniu permís.", "Accés no autoritzat");
        }

        if (missatgeText == null || missatgeText.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("missatgeErrorFlash", "El missatge no pot estar buit.");
            return "redirect:/missatge/chat/" + idSeleccion;
        }

        MissatgeChat missatge = new MissatgeChat();
        missatge.setIdMissatge(missatgeChatDao.getNextId());
        missatge.setIdSeleccion(idSeleccion);
        missatge.setDataHora(LocalDateTime.now());
        missatge.setEmissor(rolChat);
        missatge.setNomEmisor(logat.getNom() + " " + (logat.getCognoms() != null ? logat.getCognoms() : ""));
        missatge.setMissatge(missatgeText.trim());

        missatgeChatDao.addMissatge(missatge);

        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Missatge enviat correctament.");
        return "redirect:/missatge/chat/" + idSeleccion;
    }

    /**
     * GET /missatge/meues
     * Mostra totes les converses de l'usuari o assistent logat, amb opció de cerca i filtre d'estat.
     */
    @GetMapping("/meues")
    public String meuesConverses(@RequestParam(value = "cerca", required = false) String cerca,
                                 @RequestParam(value = "estatFiltre", required = false) String estatFiltre,
                                 HttpSession session, Model model) {
        UsuariOVI logat = (UsuariOVI) session.getAttribute("usuariLogat");
        if (logat == null) {
            session.setAttribute("nextUrl", "/missatge/meues");
            return "redirect:/login";
        }

        List<Map<String, Object>> converses = new ArrayList<>();
        String cercaLower = (cerca != null) ? cerca.toLowerCase() : null;

        if ("usuari".equals(logat.getRol())) {
            List<APRequest> solicituds = apRequestDao.getAPRequestsByUsuari(logat.getIdUsuari());
            for (APRequest req : solicituds) {
                List<Seleccion> seleccions = seleccionDao.getSeleccionsByRequest(req.getIdRequest());
                for (Seleccion sel : seleccions) {
                    AssistentPersonal ap = assistentPersonalDao.getAssistentPersonal(sel.getIdAssistent());
                    String nomAltre = ap != null ? ap.getNom() + " " + ap.getCognoms() : "Assistent #" + sel.getIdAssistent();
                    String reqStr = "REQ-2026-" + String.format("%04d", sel.getIdRequest());
                    
                    // Filtrat cerca
                    if (cercaLower != null && !cercaLower.trim().isEmpty() && !nomAltre.toLowerCase().contains(cercaLower) && !reqStr.toLowerCase().contains(cercaLower)) {
                        continue;
                    }
                    
                    // Filtrat estat
                    if (estatFiltre != null && !estatFiltre.trim().isEmpty() && !sel.getEstat().equalsIgnoreCase(estatFiltre)) {
                        continue;
                    }

                    Map<String, Object> conv = new HashMap<>();
                    conv.put("idSeleccion", sel.getIdSeleccion());
                    conv.put("nomAltre", nomAltre);
                    conv.put("estat", sel.getEstat());
                    conv.put("idRequest", sel.getIdRequest());
                    List<MissatgeChat> msgs = missatgeChatDao.getMissatgesBySeleccion(sel.getIdSeleccion());
                    conv.put("numMissatges", msgs.size());
                    conv.put("ultimMissatge", msgs.isEmpty() ? null : msgs.get(msgs.size() - 1));
                    converses.add(conv);
                }
            }
        } else if ("assistent".equals(logat.getRol())) {
            List<Seleccion> seleccions = seleccionDao.getSeleccionsByAssistent(logat.getIdUsuari());
            for (Seleccion sel : seleccions) {
                APRequest req = apRequestDao.getAPRequest(sel.getIdRequest());
                UsuariOVI usr = (req != null) ? usuariOVIDao.getUsuariOVI(req.getIdUsuari()) : null;
                String nomAltre = usr != null ? usr.getNom() + " " + usr.getCognoms() : "Usuari";
                String reqStr = "REQ-2026-" + String.format("%04d", sel.getIdRequest());

                // Filtrat cerca
                if (cercaLower != null && !cercaLower.trim().isEmpty() && !nomAltre.toLowerCase().contains(cercaLower) && !reqStr.toLowerCase().contains(cercaLower)) {
                    continue;
                }
                
                // Filtrat estat
                if (estatFiltre != null && !estatFiltre.trim().isEmpty() && !sel.getEstat().equalsIgnoreCase(estatFiltre)) {
                    continue;
                }

                Map<String, Object> conv = new HashMap<>();
                conv.put("idSeleccion", sel.getIdSeleccion());
                conv.put("nomAltre", nomAltre);
                conv.put("estat", sel.getEstat());
                conv.put("idRequest", sel.getIdRequest());
                List<MissatgeChat> msgs = missatgeChatDao.getMissatgesBySeleccion(sel.getIdSeleccion());
                conv.put("numMissatges", msgs.size());
                conv.put("ultimMissatge", msgs.isEmpty() ? null : msgs.get(msgs.size() - 1));
                converses.add(conv);
            }
        } else {
            return "redirect:/";
        }

        // Ordenar converses pel missatge més recent o ID de selecció (si no hi ha missatges) descendent
        converses.sort((a, b) -> {
            MissatgeChat msgA = (MissatgeChat) a.get("ultimMissatge");
            MissatgeChat msgB = (MissatgeChat) b.get("ultimMissatge");
            if (msgA != null && msgB != null) {
                return msgB.getDataHora().compareTo(msgA.getDataHora());
            } else if (msgA != null) {
                return -1;
            } else if (msgB != null) {
                return 1;
            } else {
                return Integer.compare((Integer) b.get("idSeleccion"), (Integer) a.get("idSeleccion"));
            }
        });

        model.addAttribute("converses", converses);
        model.addAttribute("cerca", cerca);
        model.addAttribute("estatFiltre", estatFiltre);
        model.addAttribute("usuariLogat", logat);
        return "missatge/list";
    }

    /**
     * Determina el rol de l'usuari logat dins d'una selecció.
     * Retorna "usuari" si és l'usuari de la sol·licitud, "assistent" si és l'assistent de la selecció.
     */
    private String determinarRolChat(UsuariOVI logat, Seleccion seleccion) {
        if ("usuari".equals(logat.getRol())) {
            APRequest req = apRequestDao.getAPRequest(seleccion.getIdRequest());
            if (req != null && req.getIdUsuari() == logat.getIdUsuari()) {
                return "usuari";
            }
        } else if ("assistent".equals(logat.getRol())) {
            if (seleccion.getIdAssistent() == logat.getIdUsuari()) {
                return "assistent";
            }
        }
        return null;
    }
}
