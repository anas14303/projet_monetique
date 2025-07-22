package com.monetique.paiement_appsb.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Statistiques factices pour la démo
        Map<String, Object> stats = new HashMap<>();
        stats.put("dailyTransactions", 42);
        stats.put("monthlyTransactions", 1250);
        stats.put("monthlyRevenue", 58420.50);
        stats.put("activeUsers", 89);
        stats.put("pendingAlerts", 5);
        stats.put("newUsersThisMonth", 12);
        stats.put("activeTerminals", 34);
        stats.put("totalTerminals", 50);
        stats.put("terminalUsage", 68); // Pourcentage
        
        // Activités récentes factices
        List<Map<String, Object>> recentActivities = new ArrayList<>();
        
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("id", "#1234");
        activity1.put("user", "Admin");
        activity1.put("action", "Connexion");
        activity1.put("timestamp", LocalDateTime.now().minusMinutes(5));
        activity1.put("status", "success");
        recentActivities.add(activity1);
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("id", "#1233");
        activity2.put("user", "Commerçant 1");
        activity2.put("action", "Paiement effectué");
        activity2.put("timestamp", LocalDateTime.now().minusMinutes(15));
        activity2.put("status", "success");
        recentActivities.add(activity2);
        
        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("id", "#1232");
        activity3.put("user", "Utilisateur 5");
        activity3.put("action", "Création de compte");
        activity3.put("timestamp", LocalDateTime.now().minusHours(1));
        activity3.put("status", "success");
        recentActivities.add(activity3);
        
        Map<String, Object> activity4 = new HashMap<>();
        activity4.put("id", "#1231");
        activity4.put("user", "Système");
        activity4.put("action", "Sauvegarde de la base de données");
        activity4.put("timestamp", LocalDateTime.now().minusHours(3));
        activity4.put("status", "success");
        recentActivities.add(activity4);
        
        model.addAttribute("stats", stats);
        model.addAttribute("recentActivities", recentActivities);
        
        return "admin/dashboard";
    }
}
