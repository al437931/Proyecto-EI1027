package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UserDao;
import es.uji.ei1027.sgovi.dao.UsuariOVIDao;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import es.uji.ei1027.sgovi.model.AssistentPersonal;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UsuariOVIDao usuariOVIDao;

    @Autowired
    private es.uji.ei1027.sgovi.dao.AssistentPersonalDao assistentPersonalDao;

    @Autowired
    private es.uji.ei1027.sgovi.services.EmailValidationService emailValidationService;

    // GET /login - mostra el formulari
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("user", new UserDetails());
        return "login/login";
    }

    // POST /login - processa les credencials
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String checkLogin(@ModelAttribute("user") UserDetails user,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        // Validació del formulari
        UserValidator userValidator = new UserValidator();
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "login/login";
        }

        // Compte especial per al tècnic (hardcoded)
        if ("tecnic@ovi.es".equals(user.getUsername()) &&
                "tecnic123".equals(user.getPassword())) {
            UsuariOVI tecnic = new UsuariOVI();
            tecnic.setNom("Tècnic");
            tecnic.setCognoms("OVI");
            tecnic.setEmail("tecnic@ovi.es");
            tecnic.setRol("tecnic");
            session.setAttribute("usuariLogat", tecnic);

            String nextUrl = (String) session.getAttribute("nextUrl");
            if (nextUrl != null) {
                session.removeAttribute("nextUrl");
                return "redirect:" + nextUrl;
            }
            return "redirect:/tecnic/solicituds";
        }

        // Comprovar si és un assistent personal
        AssistentPersonal assistent = assistentPersonalDao.getAssistentByEmail(user.getUsername());
        if (assistent != null && assistent.getPassword() != null
                && assistent.getPassword().equals(user.getPassword())) {
            if (assistent.isEstatAcceptat() == null || !assistent.isEstatAcceptat()) {
                bindingResult.rejectValue("username", "notActive",
                        "El compte d'assistent encara no ha estat activat pel tècnic.");
                return "login/login";
            }
            // Crear un UsuariOVI amb rol "assistent" per guardar a la sessió
            UsuariOVI sessionUser = new UsuariOVI();
            sessionUser.setIdUsuari(assistent.getIdAssistent());
            sessionUser.setNom(assistent.getNom());
            sessionUser.setCognoms(assistent.getCognoms());
            sessionUser.setEmail(assistent.getEmail());
            sessionUser.setRol("assistent");
            session.setAttribute("usuariLogat", sessionUser);

            String nextUrl = (String) session.getAttribute("nextUrl");
            if (nextUrl != null) {
                session.removeAttribute("nextUrl");
                return "redirect:" + nextUrl;
            }
            return "redirect:/";
        }

        // Usuari OVI normal - comprova credencials a la BD
        UsuariOVI usuari = userDao.loadUserByUsernameAndPassword(
                user.getUsername(), user.getPassword());

        if (usuari == null) {
            bindingResult.rejectValue("password", "badCredentials",
                    "Email o contrasenya incorrectes");
            return "login/login";
        }

        if (!"acceptat".equals(usuari.getEstatCompte())) {
            bindingResult.rejectValue("username", "notActive",
                    "El compte encara no ha estat activat pel tècnic");
            return "login/login";
        }

        usuari.setRol("usuari");
        session.setAttribute("usuariLogat", usuari);

        String nextUrl = (String) session.getAttribute("nextUrl");
        if (nextUrl != null) {
            session.removeAttribute("nextUrl");
            return "redirect:" + nextUrl;
        }
        return "redirect:/usuari/solicituds";
    }

    // GET /logout - invalida la sessió
    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // GET /register - mostra el formulari de registre públic
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(Model model) {
        model.addAttribute("usuariOVI", new UsuariOVI());
        return "login/register";
    }

    // POST /register - processa el registre públic
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String checkRegister(@ModelAttribute("usuariOVI") UsuariOVI usuariOVI,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Validació completa amb el validator
        UsuariOVIValidator validator = new UsuariOVIValidator();
        validator.validate(usuariOVI, bindingResult);

        if (usuariOVI.getConsentimentRGPD() == null || !usuariOVI.getConsentimentRGPD()) {
            bindingResult.rejectValue("consentimentRGPD", "rgpd.obligatori",
                    "És obligatori acceptar les condicions RGPD.");
        }

        // Verificar si l'email ja existeix globalment
        if (usuariOVI.getEmail() != null && !usuariOVI.getEmail().trim().isEmpty()
                && emailValidationService.isEmailTaken(usuariOVI.getEmail(), null, null)) {
            bindingResult.rejectValue("email", "email.duplicat",
                    "Aquest correu electrònic ja està registrat en el sistema.");
        }

        if (bindingResult.hasErrors()) {
            return "login/register";
        }

        // Generar ID
        List<UsuariOVI> tots = usuariOVIDao.getUsuariOVIs();
        int nouId = tots.stream().mapToInt(UsuariOVI::getIdUsuari).max().orElse(0) + 1;
        usuariOVI.setIdUsuari(nouId);

        usuariOVI.setDataRegistre(LocalDate.now());
        usuariOVI.setEstatCompte("pendent");
        usuariOVI.setRol("usuari");

        usuariOVIDao.addUsuariOVI(usuariOVI);

        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Registre completat correctament! El teu compte està pendent d'aprovació per part d'un tècnic de l'OVI.");
        return "redirect:/login";
    }

    // GET /register-assistent - mostra el formulari de registre públic per a assistents
    @RequestMapping(value = "/register-assistent", method = RequestMethod.GET)
    public String registerAssistentForm(Model model) {
        model.addAttribute("assistentPersonal", new AssistentPersonal());
        return "login/register-assistent";
    }

    // POST /register-assistent - processa el registre públic per a assistents
    @RequestMapping(value = "/register-assistent", method = RequestMethod.POST)
    public String checkRegisterAssistent(
            @ModelAttribute("assistentPersonal") AssistentPersonal assistent,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Validació completa
        AssistentPersonalValidator validator = new AssistentPersonalValidator(true);
        validator.validate(assistent, bindingResult);

        // Validar que l'email no estiga ja registrat globalment
        if (assistent.getEmail() != null && !assistent.getEmail().trim().isEmpty()
                && emailValidationService.isEmailTaken(assistent.getEmail(), null, null)) {
            bindingResult.rejectValue("email", "email.duplicat",
                    "Aquest correu electrònic ja està registrat en el sistema.");
        }

        if (bindingResult.hasErrors()) {
            return "login/register-assistent";
        }

        // Generar ID
        List<AssistentPersonal> tots = assistentPersonalDao.getAssistentsPersonals();
        int nouId = tots.stream().mapToInt(AssistentPersonal::getIdAssistent).max().orElse(0) + 1;
        assistent.setIdAssistent(nouId);

        // Per defecte a l'hora de registrar-se
        assistent.setEstatAcceptat(false);

        assistentPersonalDao.addAssistentPersonal(assistent);

        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Registre d'assistent completat correctament! Està pendent d'aprovació per l'OVI.");
        return "redirect:/login";
    }
}