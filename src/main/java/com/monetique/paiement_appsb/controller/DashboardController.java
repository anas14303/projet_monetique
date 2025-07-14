package com.monetique.paiement_appsb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.monetique.paiement_appsb.dto.DashboardStatsDto;
import com.monetique.paiement_appsb.repository.PaiementRepository;
import com.monetique.paiement_appsb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final UserService userService;
    private final PaiementRepository paiementRepository;

    @Autowired
    public DashboardController(UserService userService, PaiementRepository paiementRepository) {
        this.userService = userService;
        this.paiementRepository = paiementRepository;
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
        
        // Récupérer les statistiques
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setActiveUsers(userService.countActiveUsers());
        
        long totalTransactions = paiementRepository.count();
        long pendingTransactions = paiementRepository.countByStatut("EN_ATTENTE");
        
        stats.setTotalTransactions(totalTransactions);
        stats.setPendingTransactions(pendingTransactions);
        stats.setMonthlyTransactions(paiementRepository.countPaiementsThisMonth());
        
        Double totalRevenue = paiementRepository.calculateTotalAmount();
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);
        
        // Calculer la progression des tâches (par exemple, pourcentage des transactions traitées)
        int tasksProgress = totalTransactions > 0 ? 
            (int) (((double) (totalTransactions - pendingTransactions) / totalTransactions) * 100) : 0;
        stats.setTasksProgress(tasksProgress);
        
        // Pour l'instant, on initialise à 0, à adapter selon la logique métier
        // Par exemple, on pourrait compter les paiements avec un statut particulier
        stats.setPendingAlerts(0);
        
        // Ajouter les attributs au modèle
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        model.addAttribute("pageTitle", "Tableau de bord Administrateur");
        model.addAttribute("stats", stats);
        
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
