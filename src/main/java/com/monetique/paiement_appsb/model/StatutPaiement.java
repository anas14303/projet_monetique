package com.monetique.paiement_appsb.model;

/**
 * Énumération des différents statuts possibles pour un paiement.
 */
public enum StatutPaiement {
    /**
     * Paiement en attente de traitement
     */
    PENDING("En attente"),
    
    /**
     * Paiement accepté et traité avec succès
     */
    COMPLETED("Terminé"),
    
    /**
     * Paiement échoué
     */
    FAILED("Échoué"),
    
    /**
     * Statut spécial pour représenter tous les statuts
     */
    ALL("Tous les statuts"),
    
    /**
     * Paiement remboursé
     */
    REFUNDED("Remboursé"),
    
    /**
     * Paiement annulé
     */
    CANCELLED("Annulé"),
    
    /**
     * Paiement en cours de traitement
     */
    PROCESSING("En cours de traitement");
    
    private final String displayName;
    
    StatutPaiement(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Obtient le nom d'affichage du statut
     * @return Le nom d'affichage du statut
     */
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return this.displayName;
    }
}
