package com.monetique.paiement_appsb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

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
     * Tableau de bord administrateur
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/auth/login?redirect=/admin/dashboard";
        }
        
        // Vérifier si l'utilisateur a le rôle ADMIN
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
                
        if (!isAdmin) {
            return "redirect:/error/403";
        }
        
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        model.addAttribute("pageTitle", "Tableau de bord Administrateur");
        
        return "admin/dashboard";
    }
    
    /**
     * Tableau de bord utilisateur
     */
    @GetMapping("/user/dashboard")
    public String userDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/auth/login?redirect=/user/dashboard";
        }
        
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        model.addAttribute("pageTitle", "Mon Tableau de bord");
        
        return "user/dashboard";
    }
}
