package com.monetique.paiement_appsb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        if (isAuthenticated) {
            // Si l'utilisateur est connecté, rediriger vers le tableau de bord
            return "redirect:/dashboard";
        } else {
            // Si l'utilisateur n'est pas connecté, afficher la page d'accueil publique
            return "public/index";
        }
    }
}
