package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UserDao;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @Autowired
    private UserDao userDao;

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

            // Redirigir a nextUrl si existeix
            String nextUrl = (String) session.getAttribute("nextUrl");
            if (nextUrl != null) {
                session.removeAttribute("nextUrl");
                return "redirect:" + nextUrl;
            }
            return "redirect:/tecnic/solicituds";
        }

        // Usuari OVI normal - comprova credencials a la BD
        UsuariOVI usuari = userDao.loadUserByUsernameAndPassword(
                user.getUsername(), user.getPassword());

        if (usuari == null) {
            bindingResult.rejectValue("password", "badCredentials",
                    "Email o contrasenya incorrectes");
            return "login/login";
        }

        if (!"actiu".equals(usuari.getEstatCompte())) {
            bindingResult.rejectValue("username", "notActive",
                    "El compte encara no ha estat activat pel tècnic");
            return "login/login";
        }

        usuari.setRol("usuari");
        session.setAttribute("usuariLogat", usuari);

        // Redirigir a nextUrl si existeix
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
}
