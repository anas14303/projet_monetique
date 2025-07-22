package com.monetique.paiement_appsb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    public DashboardController() {
    }

    /**
     * Redirige vers le tableau de bord approprié en fonction du rôle de l'utilisateur
     */
    @GetMapping("/dashboard")
    public String redirectToAppropriateDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/auth/login?redirect=/dashboard";
        }
        
        // Vérifier si l'utilisateur a le rôle ADMIN
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
                
        if (isAdmin) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/user/dashboard";
        }
    }
    
    /**
     * Tableau de bord utilisateur
     */
    @GetMapping("/user/dashboard")
    public String userDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/auth/login?redirect=/user/dashboard";
        }
        
        return "user/dashboard";
    }
}
