package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UsuariOVIDao;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/usuariovi")
public class UsuariOVIController {

    private UsuariOVIDao usuariOVIDao;
    private es.uji.ei1027.sgovi.services.EmailValidationService emailValidationService;
    private es.uji.ei1027.sgovi.dao.ComunicacioUsuariOVIPAPDao comunicacioDao;

    @Autowired
    public void setUsuariOVIDao(UsuariOVIDao usuariOVIDao) {
        this.usuariOVIDao = usuariOVIDao;
    }

    @Autowired
    public void setEmailValidationService(es.uji.ei1027.sgovi.services.EmailValidationService emailValidationService) {
        this.emailValidationService = emailValidationService;
    }

    @Autowired
    public void setComunicacioDao(es.uji.ei1027.sgovi.dao.ComunicacioUsuariOVIPAPDao comunicacioDao) {
        this.comunicacioDao = comunicacioDao;
    }

    @GetMapping("/list")
    public String listUsuariOVIs(@RequestParam(value = "cerca", required = false) String cerca, Model model) {
        List<UsuariOVI> usuaris = usuariOVIDao.getUsuariOVIs();
        String cercaLower = (cerca != null) ? cerca.toLowerCase() : null;

        if (cercaLower != null && !cercaLower.trim().isEmpty()) {
            usuaris = usuaris.stream().filter(u -> {
                String nom = u.getNom() != null ? u.getNom().toLowerCase() : "";
                String cognoms = u.getCognoms() != null ? u.getCognoms().toLowerCase() : "";
                String email = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                String estat = u.getEstatCompte() != null ? u.getEstatCompte().toLowerCase() : "";
                return nom.contains(cercaLower) || cognoms.contains(cercaLower) || email.contains(cercaLower) || estat.contains(cercaLower);
            }).toList();
        }
        
        // Convert to a mutable list before sorting
        List<UsuariOVI> mutableUsuaris = new java.util.ArrayList<>(usuaris);
        mutableUsuaris.sort((a, b) -> Integer.compare(b.getIdUsuari(), a.getIdUsuari()));

        model.addAttribute("usuaris", mutableUsuaris);
        model.addAttribute("cerca", cerca);
        return "usuariovi/list";
    }

    @GetMapping("/add")
    public String addUsuariForm(Model model) {
        model.addAttribute("usuariOVI", new UsuariOVI());
        return "usuariovi/add";
    }

    @PostMapping("/add")
    public String addUsuari(@ModelAttribute("usuariOVI") UsuariOVI usuariOVI,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // Validar consentiment RGPD obligatori
        if (usuariOVI.getConsentimentRGPD() == null || !usuariOVI.getConsentimentRGPD()) {
            bindingResult.rejectValue("consentimentRGPD", "rgpd.obligatori",
                    "El consentiment RGPD és obligatori per al registre.");
            return "usuariovi/add";
        }

        // Verificar si l'email ja existeix globalment
        if (emailValidationService.isEmailTaken(usuariOVI.getEmail(), null, null)) {
            bindingResult.rejectValue("email", "email.duplicat",
                    "Aquest correu electrònic ja està registrat en el sistema (com a usuari, assistent o formador).");
            return "usuariovi/add";
        }

        if (bindingResult.hasErrors()) {
            return "usuariovi/add";
        }

        // Generar ID automàtic
        List<UsuariOVI> tots = usuariOVIDao.getUsuariOVIs();
        int nouId = tots.stream().mapToInt(UsuariOVI::getIdUsuari).max().orElse(0) + 1;
        usuariOVI.setIdUsuari(nouId);

        // Data de registre automàtica
        if (usuariOVI.getDataRegistre() == null) {
            usuariOVI.setDataRegistre(LocalDate.now());
        }

        // Estat compte per defecte: pendent
        if (usuariOVI.getEstatCompte() == null || usuariOVI.getEstatCompte().isEmpty()) {
            usuariOVI.setEstatCompte("pendent");
        }

        // Generar contrasenya inicial aleatòria
        String passwordInicial = UUID.randomUUID().toString().substring(0, 8);
        usuariOVI.setPassword(passwordInicial);

        usuariOVIDao.addUsuariOVI(usuariOVI);

        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Usuari '" + usuariOVI.getNom() + " " + usuariOVI.getCognoms() +
                "' registrat correctament (ID: " + nouId +
                "). Contrasenya inicial: " + passwordInicial);
        return "redirect:/usuariovi/list";
    }

    @GetMapping("/update/{id}")
    public String updateUsuariForm(@PathVariable int id, Model model) {
        UsuariOVI usuari = usuariOVIDao.getUsuariOVI(id);
        if (usuari == null) {
            throw new SgoviException("Usuari no trobat", "Error");
        }
        model.addAttribute("usuariOVI", usuari);
        return "usuariovi/update";
    }

    @PostMapping("/update")
    public String updateUsuari(@ModelAttribute("usuariOVI") UsuariOVI usuariOVI,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "usuariovi/update";
        }

        // Verificar si l'email ja existeix en un altre usuari globalment
        if (emailValidationService.isEmailTaken(usuariOVI.getEmail(), "usuari", usuariOVI.getIdUsuari())) {
            bindingResult.rejectValue("email", "email.duplicat",
                    "Aquest correu electrònic ja està en ús per una altra persona.");
            return "usuariovi/update";
        }

        usuariOVIDao.updateUsuariOVI(usuariOVI);
        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Dades de l'usuari actualitzades correctament.");
        return "redirect:/usuariovi/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUsuari(@PathVariable int id,
                               RedirectAttributes redirectAttributes) {
        try {
            usuariOVIDao.deleteUsuariOVI(id);
            redirectAttributes.addFlashAttribute("missatgeExitFlash",
                    "Usuari eliminat correctament.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("missatgeErrorFlash",
                    "No es pot eliminar aquest usuari perquè té sol·licituds o historial associat. Es recomana canviar el seu estat a 'rebutjat' per desactivar-lo.");
        }
        return "redirect:/usuariovi/list";
    }

    @GetMapping("/rebutjar/{id}")
    public String rebutjarUsuariForm(@PathVariable int id, Model model) {
        UsuariOVI usuari = usuariOVIDao.getUsuariOVI(id);
        if (usuari == null) {
            return "redirect:/usuariovi/list";
        }
        model.addAttribute("usuari", usuari);
        return "usuariovi/rebutjar";
    }

    @PostMapping("/activar/{id}")
    public String activarUsuari(@PathVariable int id,
                                RedirectAttributes redirectAttributes) {
        UsuariOVI usuari = usuariOVIDao.getUsuariOVI(id);
        if (usuari != null) {
            usuari.setEstatCompte("acceptat");
            usuariOVIDao.updateUsuariOVI(usuari);
            redirectAttributes.addFlashAttribute("missatgeExitFlash",
                    "Compte de '" + usuari.getNom() + " " + usuari.getCognoms() + "' activat correctament.");
        }
        return "redirect:/usuariovi/list";
    }

    @PostMapping("/rebutjar/{id}")
    public String rebutjarUsuari(@PathVariable int id,
                                 @RequestParam("motiu") String motiu,
                                 RedirectAttributes redirectAttributes) {
        UsuariOVI usuari = usuariOVIDao.getUsuariOVI(id);
        if (usuari != null) {
            usuari.setEstatCompte("rebutjat");
            usuari.setMotiuRebuig(motiu);
            usuariOVIDao.updateUsuariOVI(usuari);

            // Generar correu simulat
            es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP c = new es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP();
            c.setIdComunicacio(comunicacioDao.getNextId());
            c.setDestinatari(usuari.getEmail());
            c.setAssumpte("Sol·licitud de registre rebutjada");
            c.setDataHora(java.time.LocalDateTime.now());
            c.setEmissor("tecnic@ovi.es");
            c.setMissatge("Estimat/ada " + usuari.getNom() + ",\n\n" +
                    "La seua sol·licitud de registre a OVI ha estat REBUTJADA.\n\n" +
                    "Motiu: " + motiu + "\n\n" +
                    "Atentament,\nTècnic OVI");
            c.setTipusComunicacio("rebuig_registre");
            comunicacioDao.addComunicacio(c);

            redirectAttributes.addFlashAttribute("missatgeExitFlash",
                    "S'ha rebutjat la sol·licitud de registre de l'usuari correctament.");
        }
        return "redirect:/usuariovi/list";
    }
}
