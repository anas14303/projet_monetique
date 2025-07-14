package com.monetique.paiement_appsb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private long activeUsers;
    private long totalTransactions;
    private long pendingTransactions;
    private long monthlyTransactions;
    private double totalRevenue;
    private int tasksProgress; // Pourcentage de progression des tâches
    private int pendingAlerts; // Nombre d'alertes en attente
    
    // Getter/Setter pour pendingAlerts
    public int getPendingAlerts() {
        return pendingAlerts;
    }
    
    public void setPendingAlerts(int pendingAlerts) {
        this.pendingAlerts = pendingAlerts;
    }
    
    // Méthodes utilitaires pour les pourcentages
    public double getSuccessRate() {
        return totalTransactions > 0 ? 
            ((double)(totalTransactions - pendingTransactions) / totalTransactions) * 100 : 0;
    }
    
    // Getter/Setter pour tasksProgress
    public int getTasksProgress() {
        return tasksProgress;
    }
    
    public void setTasksProgress(int tasksProgress) {
        this.tasksProgress = tasksProgress;
    }
}
