package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.AssistentPersonalDao;
import es.uji.ei1027.sgovi.dao.APRequestDao;
import es.uji.ei1027.sgovi.dao.RegistreContracteDao;
import es.uji.ei1027.sgovi.model.AssistentPersonal;
import es.uji.ei1027.sgovi.model.RegistreContracte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.dao.UsuariOVIDao;
import es.uji.ei1027.sgovi.model.APRequest;
import es.uji.ei1027.sgovi.model.Seleccion;
import es.uji.ei1027.sgovi.model.UsuariOVI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/registrecontracte")
public class RegistreContracteController {

    private static final String UPLOAD_DIR = "uploads/contractes/";

    private RegistreContracteDao registreContracteDao;
    private AssistentPersonalDao assistentPersonalDao;
    private APRequestDao apRequestDao;
    private SeleccionDao seleccionDao;
    private UsuariOVIDao usuariOVIDao;

    @Autowired
    public void setRegistreContracteDao(RegistreContracteDao registreContracteDao) {
        this.registreContracteDao = registreContracteDao;
    }

    @Autowired
    public void setAssistentPersonalDao(AssistentPersonalDao assistentPersonalDao) {
        this.assistentPersonalDao = assistentPersonalDao;
    }

    @Autowired
    public void setAPRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @Autowired
    public void setSeleccionDao(SeleccionDao seleccionDao) {
        this.seleccionDao = seleccionDao;
    }

    @Autowired
    public void setUsuariOVIDao(UsuariOVIDao usuariOVIDao) {
        this.usuariOVIDao = usuariOVIDao;
    }

    // Crea mapes per mostrar noms en comptes d'IDs
    private Map<Integer, String> getMapaNomsAssistents() {
        Map<Integer, String> mapa = new HashMap<>();
        for (AssistentPersonal a : assistentPersonalDao.getAssistentsPersonals()) {
            mapa.put(a.getIdAssistent(), a.getNom() + " " + a.getCognoms());
        }
        return mapa;
    }

    private Map<Integer, String> getMapaNomsUsuaris() {
        Map<Integer, String> mapa = new HashMap<>();
        for (APRequest req : apRequestDao.getAPRequests()) {
            UsuariOVI u = usuariOVIDao.getUsuariOVI(req.getIdUsuari());
            if (u != null) {
                mapa.put(req.getIdRequest(), u.getNom() + " " + u.getCognoms());
            }
        }
        return mapa;
    }

    @GetMapping("/list")
    public String listRegistres(@RequestParam(value = "cerca", required = false) String cerca, Model model) {
        List<RegistreContracte> registres = registreContracteDao.getRegistresContracte();
        Map<Integer, String> nomsAssistents = getMapaNomsAssistents();
        Map<Integer, String> nomsUsuaris = getMapaNomsUsuaris();
        
        String cercaLower = (cerca != null) ? cerca.toLowerCase() : null;

        if (cercaLower != null && !cercaLower.trim().isEmpty()) {
            registres = registres.stream().filter(u -> {
                String reqStr = "req-2026-" + String.format("%04d", u.getIdRequest());
                String nomAss = nomsAssistents.getOrDefault(u.getIdAssistent(), "").toLowerCase();
                String nomUsr = nomsUsuaris.getOrDefault(u.getIdRequest(), "").toLowerCase();
                return reqStr.contains(cercaLower) || nomAss.contains(cercaLower) || nomUsr.contains(cercaLower);
            }).toList();
        }
        
        List<RegistreContracte> mutableRegistres = new java.util.ArrayList<>(registres);
        mutableRegistres.sort((a, b) -> Integer.compare(b.getIdContracte(), a.getIdContracte()));

        model.addAttribute("registres", mutableRegistres);
        model.addAttribute("nomsAssistents", nomsAssistents);
        model.addAttribute("nomsUsuaris", nomsUsuaris);
        model.addAttribute("cerca", cerca);
        return "registrecontracte/list";
    }

    @GetMapping("/add")
    public String addRegistreForm(Model model) {
        model.addAttribute("registreContracte", new RegistreContracte());
        model.addAttribute("assistents", assistentPersonalDao.getAssistentsPersonals());
        model.addAttribute("solicituds", apRequestDao.getAPRequests());
        return "registrecontracte/add";
    }

    @PostMapping("/add")
    public String addRegistre(@ModelAttribute("registreContracte") RegistreContracte registre,
                              BindingResult bindingResult,
                              Model model,
                              @RequestParam(value = "fitxerPdf", required = false) MultipartFile fitxerPdf,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("assistents", assistentPersonalDao.getAssistentsPersonals());
            model.addAttribute("solicituds", apRequestDao.getAPRequests());
            return "registrecontracte/add";
        }

        // Validar que la data fi >= data inici
        if (registre.getDataFi() != null && registre.getDataInici() != null
                && registre.getDataFi().isBefore(registre.getDataInici())) {
            bindingResult.rejectValue("dataFi", "dates.invalid",
                    "La data de fi ha de ser posterior a la data d'inici.");
            model.addAttribute("assistents", assistentPersonalDao.getAssistentsPersonals());
            model.addAttribute("solicituds", apRequestDao.getAPRequests());
            return "registrecontracte/add";
        }

        // Generar ID automàtic
        List<RegistreContracte> tots = registreContracteDao.getRegistresContracte();
        int nouId = tots.stream().mapToInt(RegistreContracte::getIdContracte).max().orElse(0) + 1;
        registre.setIdContracte(nouId);

        // Pujar el PDF si s'ha seleccionat
        if (fitxerPdf != null && !fitxerPdf.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);
                String fileName = "contracte_" + nouId + "_" + fitxerPdf.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(fitxerPdf.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                registre.setPdfContracte(fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("missatgeErrorFlash",
                        "Error al pujar el fitxer PDF: " + e.getMessage());
                return "redirect:/registrecontracte/add";
            }
        }

        registreContracteDao.addRegistreContracte(registre);
        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Contracte #" + nouId + " registrat correctament.");
        return "redirect:/registrecontracte/list";
    }

    @GetMapping("/update/{id}")
    public String updateRegistreForm(@PathVariable int id, Model model) {
        RegistreContracte registre = registreContracteDao.getRegistreContracte(id);
        if (registre == null) {
            throw new SgoviException("Contracte no trobat", "Error");
        }
        model.addAttribute("registreContracte", registre);
        model.addAttribute("assistents", assistentPersonalDao.getAssistentsPersonals());
        model.addAttribute("solicituds", apRequestDao.getAPRequests());
        return "registrecontracte/update";
    }

    @PostMapping("/update")
    public String updateRegistre(@ModelAttribute("registreContracte") RegistreContracte registre,
                                 BindingResult bindingResult,
                                 Model model,
                                 @RequestParam(value = "fitxerPdf", required = false) MultipartFile fitxerPdf,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("assistents", assistentPersonalDao.getAssistentsPersonals());
            model.addAttribute("solicituds", apRequestDao.getAPRequests());
            return "registrecontracte/update";
        }

        // Validar que la data fi >= data inici
        if (registre.getDataFi() != null && registre.getDataInici() != null
                && registre.getDataFi().isBefore(registre.getDataInici())) {
            bindingResult.rejectValue("dataFi", "dates.invalid",
                    "La data de fi ha de ser posterior a la data d'inici.");
            model.addAttribute("assistents", assistentPersonalDao.getAssistentsPersonals());
            model.addAttribute("solicituds", apRequestDao.getAPRequests());
            return "registrecontracte/update";
        }

        // Pujar el PDF si s'ha seleccionat un de nou
        if (fitxerPdf != null && !fitxerPdf.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);
                String fileName = "contracte_" + registre.getIdContracte() + "_" + fitxerPdf.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(fitxerPdf.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                registre.setPdfContracte(fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("missatgeErrorFlash",
                        "Error al pujar el fitxer PDF: " + e.getMessage());
                return "redirect:/registrecontracte/update/" + registre.getIdContracte();
            }
        }

        registreContracteDao.updateRegistreContracte(registre);
        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Contracte actualitzat correctament.");
        return "redirect:/registrecontracte/list";
    }

    // Descarregar PDF del contracte
    @GetMapping("/pdf/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> descarregarPdf(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                        .body(resource);
            } else {
                throw new SgoviException("Fitxer no trobat: " + fileName, "Error");
            }
        } catch (Exception e) {
            throw new SgoviException("Error al descarregar el fitxer: " + e.getMessage(), "Error");
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteRegistre(@PathVariable int id,
                                 RedirectAttributes redirectAttributes) {
        registreContracteDao.deleteRegistreContracte(id);
        redirectAttributes.addFlashAttribute("missatgeExitFlash",
                "Contracte eliminat correctament.");
        return "redirect:/registrecontracte/list";
    }

    // API REST: retorna les sol·licituds assignades a un assistent concret (via seleccion)
    @GetMapping("/api/solicituds-per-assistent/{idAssistent}")
    @ResponseBody
    public List<Map<String, Object>> getSolicitudsPerAssistent(@PathVariable Integer idAssistent) {
        List<Seleccion> seleccions = seleccionDao.getSeleccions().stream()
                .filter(s -> s.getIdAssistent() == idAssistent)
                .toList();

        List<Map<String, Object>> resultat = new java.util.ArrayList<>();
        for (Seleccion sel : seleccions) {
            APRequest req = apRequestDao.getAPRequest(sel.getIdRequest());
            if (req == null) continue;
            UsuariOVI usuari = usuariOVIDao.getUsuariOVI(req.getIdUsuari());
            String nomUsuari = (usuari != null) ? usuari.getNom() + " " + usuari.getCognoms() : "Usuari #" + req.getIdUsuari();

            Map<String, Object> item = new HashMap<>();
            item.put("idRequest", req.getIdRequest());
            item.put("tipusAssistencia", req.getTipusAssistencia());
            item.put("estat", req.getEstat());
            item.put("nomUsuari", nomUsuari);
            item.put("estatSeleccion", sel.getEstat());
            resultat.add(item);
        }
        return resultat;
    }
}
