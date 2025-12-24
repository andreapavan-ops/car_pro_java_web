package com.carpro.controller;

import com.carpro.model.Utente;
import com.carpro.repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UtenteRepository utenteRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Optional<Utente> utente = utenteRepository.findByUsernameAndPassword(username, password);

        if (utente.isPresent()) {
            session.setAttribute("utente", utente.get());
            return "redirect:/catalogo";
        } else {
            model.addAttribute("errore", "Username o password non validi");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/catalogo";
    }
}
