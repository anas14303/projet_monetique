package com.monetique.paiement_appsb.service.impl;

import com.monetique.paiement_appsb.dto.DashboardStatsDto;
import com.monetique.paiement_appsb.repository.PaiementRepository;
import com.monetique.paiement_appsb.repository.UtilisateurRepository;
import com.monetique.paiement_appsb.service.StatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final UtilisateurRepository utilisateurRepository;
    private final PaiementRepository paiementRepository;

    public StatisticsServiceImpl(UtilisateurRepository utilisateurRepository, 
                               PaiementRepository paiementRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.paiementRepository = paiementRepository;
    }

    @Override
    public DashboardStatsDto getDashboardStatistics() {
        DashboardStatsDto stats = new DashboardStatsDto();
        
        // Récupérer le nombre total d'utilisateurs
        long totalUsers = utilisateurRepository.count();
        stats.setActiveUsers(totalUsers); // Pour l'instant, on utilise le nombre total d'utilisateurs
        
        // Récupérer le nombre total de paiements
        long totalTransactions = paiementRepository.count();
        stats.setTotalTransactions(totalTransactions);
        
        // Récupérer le nombre de paiements en attente
        long pendingTransactions = paiementRepository.countByStatus("EN_ATTENTE");
        stats.setPendingTransactions(pendingTransactions);
        
        // Récupérer le nombre de paiements du mois
        Date startOfMonth = Date.from(LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .atZone(ZoneId.systemDefault())
            .toInstant());
            
        long monthlyTransactions = paiementRepository.countByDateAfter(startOfMonth);
        stats.setMonthlyTransactions(monthlyTransactions);
        
        // Calculer le revenu total (exemple simplifié)
        // Note: Implémentez cette méthode dans PaiementRepository si nécessaire
        // Double totalRevenue = paiementRepository.sumMontantByStatus("TERMINE");
        // stats.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);
        stats.setTotalRevenue(0.0); // Valeur temporaire
        
        // Nombre d'alertes en attente (exemple)
        stats.setPendingAlerts(5); // À implémenter avec la logique réelle
        
        // Progression des tâches (exemple)
        stats.setTasksProgress(75); // À implémenter avec la logique réelle
        
        return stats;
    }
    

}
