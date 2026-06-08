package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.AssistentPersonalDao;
import es.uji.ei1027.sgovi.model.AssistentPersonal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/assistentpersonal")
public class AssistentPersonalController {

    private AssistentPersonalDao assistentPersonalDao;
    private es.uji.ei1027.sgovi.services.EmailValidationService emailValidationService;
    private es.uji.ei1027.sgovi.dao.ComunicacioUsuariOVIPAPDao comunicacioDao;

    @Autowired
    public void setAssistentPersonalDao(AssistentPersonalDao assistentPersonalDao) {
        this.assistentPersonalDao = assistentPersonalDao;
    }

    @Autowired
    public void setEmailValidationService(es.uji.ei1027.sgovi.services.EmailValidationService emailValidationService) {
        this.emailValidationService = emailValidationService;
    }

    @Autowired
    public void setComunicacioDao(es.uji.ei1027.sgovi.dao.ComunicacioUsuariOVIPAPDao comunicacioDao) {
        this.comunicacioDao = comunicacioDao;
    }

    // LISTAR
    @GetMapping("/list")
    public String listAssistentsPersonals(@RequestParam(value = "cerca", required = false) String cerca, Model model) {
        List<AssistentPersonal> assistents = assistentPersonalDao.getAssistentsPersonals();
        String cercaLower = (cerca != null) ? cerca.toLowerCase() : null;

        if (cercaLower != null && !cercaLower.trim().isEmpty()) {
            assistents = assistents.stream().filter(u -> {
                String nom = u.getNom() != null ? u.getNom().toLowerCase() : "";
                String cognoms = u.getCognoms() != null ? u.getCognoms().toLowerCase() : "";
                String email = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                String tipus = u.getTipusAssistent() != null ? u.getTipusAssistent().toLowerCase() : "";
                return nom.contains(cercaLower) || cognoms.contains(cercaLower) || email.contains(cercaLower) || tipus.contains(cercaLower);
            }).toList();
        }
        
        List<AssistentPersonal> mutableAssistents = new java.util.ArrayList<>(assistents);
        mutableAssistents.sort((a, b) -> Integer.compare(b.getIdAssistent(), a.getIdAssistent()));

        model.addAttribute("assistents", mutableAssistents);
        model.addAttribute("cerca", cerca);
        return "assistentpersonal/list";
    }

    // AÑADIR (GET)
    @GetMapping("/add")
    public String addAssistentForm(Model model) {
        model.addAttribute("assistentPersonal", new AssistentPersonal());
        return "assistentpersonal/add";
    }

    // AÑADIR (POST)
    @PostMapping("/add")
    public String addAssistent(@ModelAttribute("assistentPersonal") AssistentPersonal assistent,
                               BindingResult bindingResult) {

        if (emailValidationService.isEmailTaken(assistent.getEmail(), null, null)) {
            bindingResult.rejectValue("email", "email.duplicat",
                    "Aquest correu electrònic ja està registrat en el sistema (com a usuari, assistent o formador).");
        }

        if (bindingResult.hasErrors()) {
            return "assistentpersonal/add";
        }

        assistentPersonalDao.addAssistentPersonal(assistent);
        return "redirect:/assistentpersonal/list";
    }

    // ACTUALIZAR (GET)
    @GetMapping("/update/{id}")
    public String updateAssistentForm(@PathVariable int id, Model model) {
        model.addAttribute("assistentPersonal", assistentPersonalDao.getAssistentPersonal(id));
        return "assistentpersonal/update";
    }

    // ACTUALIZAR (POST)
    @PostMapping("/update")
    public String updateAssistent(@ModelAttribute("assistentPersonal") AssistentPersonal assistent,
                                  BindingResult bindingResult) {

        if (emailValidationService.isEmailTaken(assistent.getEmail(), "assistent", assistent.getIdAssistent())) {
            bindingResult.rejectValue("email", "email.duplicat",
                    "Aquest correu electrònic ja està en ús per una altra persona.");
        }

        if (bindingResult.hasErrors()) {
            return "assistentpersonal/update";
        }

        assistentPersonalDao.updateAssistentPersonal(assistent);
        return "redirect:/assistentpersonal/list";
    }

    // CONTRASEÑA (GET)
    @GetMapping("/password/{id}")
    public String changePasswordForm(@PathVariable int id, Model model) {
        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(id);
        if (assistent == null) {
            return "redirect:/assistentpersonal/list";
        }
        // Pasamos el objeto al modelo
        model.addAttribute("assistentPersonal", assistent);
        return "assistentpersonal/password";
    }

    // CONTRASEÑA (POST)
    @PostMapping("/password")
    public String updatePassword(@RequestParam("idAssistent") int idAssistent,
                                 @RequestParam("password") String password,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("missatgeErrorFlash", "La contrasenya no pot estar buida.");
            return "redirect:/assistentpersonal/password/" + idAssistent;
        }
        assistentPersonalDao.updatePassword(idAssistent, password.trim());
        redirectAttributes.addFlashAttribute("missatgeExitFlash", "Contrasenya actualitzada correctament.");
        return "redirect:/assistentpersonal/list";
    }

    // BORRAR
    @GetMapping("/delete/{id}")
    public String deleteAssistent(@PathVariable int id) {
        assistentPersonalDao.deleteAssistentPersonal(id);
        return "redirect:/assistentpersonal/list";
    }

    @GetMapping("/rebutjar/{id}")
    public String rebutjarAssistentForm(@PathVariable int id, Model model) {
        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(id);
        if (assistent == null) {
            return "redirect:/assistentpersonal/list";
        }
        model.addAttribute("assistent", assistent);
        return "assistentpersonal/rebutjar";
    }

    @PostMapping("/rebutjar/{id}")
    public String rebutjarAssistent(@PathVariable int id,
                                    @RequestParam("motiu") String motiu,
                                    org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        AssistentPersonal assistent = assistentPersonalDao.getAssistentPersonal(id);
        if (assistent != null) {
            assistent.setEstatAcceptat(false);
            assistent.setMotiuRebuig(motiu);
            assistentPersonalDao.updateAssistentPersonal(assistent);

            // Generar correu simulat
            es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP c = new es.uji.ei1027.sgovi.model.ComunicacioUsuariOVIPAP();
            c.setIdComunicacio(comunicacioDao.getNextId());
            c.setDestinatari(assistent.getEmail());
            c.setAssumpte("Sol·licitud de registre rebutjada");
            c.setDataHora(java.time.LocalDateTime.now());
            c.setEmissor("tecnic@ovi.es");
            c.setMissatge("Estimat/ada " + assistent.getNom() + ",\n\n" +
                    "La seua sol·licitud de registre com a Assistent Personal a OVI ha estat REBUTJADA.\n\n" +
                    "Motiu: " + motiu + "\n\n" +
                    "Atentament,\nTècnic OVI");
            c.setTipusComunicacio("rebuig_registre");
            comunicacioDao.addComunicacio(c);

            redirectAttributes.addFlashAttribute("missatgeExitFlash",
                    "S'ha rebutjat la sol·licitud de registre de l'assistent correctament.");
        }
        return "redirect:/assistentpersonal/list";
    }
}
