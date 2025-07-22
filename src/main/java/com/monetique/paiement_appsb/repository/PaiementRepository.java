package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.Paiement;
import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.model.StatutPaiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    
    long countByStatut(StatutPaiement statut);
    
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.statut = 'COMPLETED'")
    Double calculateTotalAmount();
    
    @Query("SELECT COUNT(p) FROM Paiement p WHERE YEAR(p.date) = YEAR(CURRENT_DATE) AND MONTH(p.date) = MONTH(CURRENT_DATE)")
    long countPaiementsThisMonth();

    // Recherche avancée
    @Query(value = "SELECT p FROM Paiement p", countQuery = "SELECT COUNT(p) FROM Paiement p")
    List<Paiement> searchPaiements(
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin,
            @Param("montantMin") Long montantMin,
            @Param("montantMax") Long montantMax,
            @Param("statut") StatutPaiement statut,
            @Param("nomCommercant") String nomCommercant
    );

    // Filter by date range
    @Query("SELECT p FROM Paiement p WHERE (:startDate IS NULL OR p.date >= :startDate) " +
            "AND (:endDate IS NULL OR p.date <= :endDate)")
    Page<Paiement> findByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    // Filter by multiple criteria
    @Query("SELECT p FROM Paiement p " +
            "WHERE (:statut IS NULL OR p.statut = :statut) " +
            "AND (:commercant IS NULL OR p.commercant = :commercant) " +
            "AND (:typeCarte IS NULL OR p.carteBancaire.type = :typeCarte) " +
            "AND (:startDate IS NULL OR p.date >= :startDate) " +
            "AND (:endDate IS NULL OR p.date <= :endDate)")
    Page<Paiement> filterPaiements(
            @Param("statut") StatutPaiement statut,
            @Param("commercant") Commercant commercant,
            @Param("typeCarte") String typeCarte,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    // Filter by commercant
    /**
     * Count payments by status
     * @param status the status to filter by
     * @return count of payments with the given status
     */
    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statut = :status")
    long countByStatus(@Param("status") String status);
    
    /**
     * Count payments created after the given date
     * @param date the date to compare with
     * @return count of payments created after the date
     */
    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.date >= :date")
    long countByDateAfter(@Param("date") Date date);
    
    @Query("SELECT p FROM Paiement p WHERE (:commercant IS NULL OR p.commercant = :commercant)")
    Page<Paiement> findByCommercant(
            @Param("commercant") Commercant commercant,
            Pageable pageable
    );

    // Filter by type de carte
    @Query("SELECT p FROM Paiement p WHERE (:typeCarte IS NULL OR p.typeCarte = :typeCarte)")
    Page<Paiement> findByTypeCarte(
            @Param("typeCarte") String typeCarte,
            Pageable pageable
    );

    // Filter by statut
    @Query("SELECT p FROM Paiement p WHERE p.statut = :statut")
    Page<Paiement> findByStatut(
            @Param("statut") StatutPaiement statut,
            Pageable pageable
    );
}
