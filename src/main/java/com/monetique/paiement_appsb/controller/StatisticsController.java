package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.dto.DashboardStatsDto;
import com.monetique.paiement_appsb.service.StatisticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String showStatisticsDashboard(Model model) {
        // Récupérer les statistiques du tableau de bord
        DashboardStatsDto stats = statisticsService.getDashboardStatistics();
        
        // Ajouter les statistiques au modèle
        model.addAttribute("stats", stats);
        
        // Ajouter d'autres données nécessaires pour les graphiques
        // Ces données devraient être récupérées depuis le service
        // model.addAttribute("chartData", statisticsService.getChartData());
        
        return "admin/statistics";
    }
    
    // Ajouter d'autres méthodes pour les appels AJAX si nécessaire
    // Par exemple, pour le filtrage par date
}
