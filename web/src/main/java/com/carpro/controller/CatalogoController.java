package com.carpro.controller;

import com.carpro.model.Auto;
import com.carpro.repository.AutoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class CatalogoController {

    @Autowired
    private AutoRepository autoRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/catalogo";
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) String search, Model model) {
        List<Auto> auto;

        if (search != null && !search.trim().isEmpty()) {
            auto = autoRepository.searchByMarcaOrModello(search.trim());
            model.addAttribute("search", search);
        } else {
            auto = autoRepository.findAll();
        }

        model.addAttribute("auto", auto);
        model.addAttribute("totale", auto.size());

        return "catalogo";
    }

    @GetMapping("/auto/{id}")
    public String dettaglioAuto(@PathVariable Long id, Model model) {
        Optional<Auto> auto = autoRepository.findById(id);

        if (auto.isPresent()) {
            model.addAttribute("auto", auto.get());
            return "dettaglio";
        }

        return "redirect:/catalogo";
    }

    // ====== CRUD Operations (richiede login) ======

    @GetMapping("/auto/nuova")
    public String nuovaAutoForm(HttpSession session, Model model) {
        if (session.getAttribute("utente") == null) {
            return "redirect:/login";
        }
        model.addAttribute("auto", new Auto());
        return "form-auto";
    }

    @PostMapping("/auto/nuova")
    public String nuovaAuto(@ModelAttribute Auto auto, HttpSession session, Model model) {
        if (session.getAttribute("utente") == null) {
            return "redirect:/login";
        }

        try {
            autoRepository.save(auto);
            return "redirect:/catalogo";
        } catch (Exception e) {
            model.addAttribute("errore", "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("auto", auto);
            return "form-auto";
        }
    }

    @GetMapping("/auto/{id}/modifica")
    public String modificaAutoForm(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("utente") == null) {
            return "redirect:/login";
        }

        Optional<Auto> auto = autoRepository.findById(id);
        if (auto.isPresent()) {
            model.addAttribute("auto", auto.get());
            return "form-auto";
        }

        return "redirect:/catalogo";
    }

    @PostMapping("/auto/{id}/modifica")
    public String modificaAuto(@PathVariable Long id, @ModelAttribute Auto auto, HttpSession session, Model model) {
        if (session.getAttribute("utente") == null) {
            return "redirect:/login";
        }

        try {
            auto.setId(id);
            autoRepository.save(auto);
            return "redirect:/auto/" + id;
        } catch (Exception e) {
            model.addAttribute("errore", "Errore nella modifica: " + e.getMessage());
            model.addAttribute("auto", auto);
            return "form-auto";
        }
    }

    @PostMapping("/auto/{id}/elimina")
    public String eliminaAuto(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("utente") == null) {
            return "redirect:/login";
        }

        autoRepository.deleteById(id);
        return "redirect:/catalogo";
    }
}
