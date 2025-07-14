package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.dto.DashboardStatsDto;

public interface StatisticsService {
    /**
     * Récupère les statistiques pour le tableau de bord
     * @return Un objet DashboardStatsDto contenant les statistiques
     */
    DashboardStatsDto getDashboardStatistics();
    
    // Autres méthodes pour les statistiques détaillées
    // Map<String, Object> getChartData(String dateRange);
    // Object getTransactionStats(Date startDate, Date endDate);
    // etc.
}
