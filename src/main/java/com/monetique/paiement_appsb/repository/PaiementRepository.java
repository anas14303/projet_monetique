package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.Paiement;
import com.monetique.paiement_appsb.model.Commercant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    // Main filter query
    @Query("SELECT p FROM Paiement p " +
            "WHERE (:statut IS NULL OR p.statut = :statut) " +
            "AND (:commercant IS NULL OR p.commercant = :commercant) " +
            "AND (:typeCarte IS NULL OR p.typeCarte = :typeCarte) " +
            "AND (:startDate IS NULL OR p.date >= :startDate) " +
            "AND (:endDate IS NULL OR p.date <= :endDate)")
    Page<Paiement> filterPaiements(
            @Param("statut") String statut,
            @Param("commercant") Commercant commercant,
            @Param("typeCarte") String typeCarte,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    // Filter by date range
    @Query("SELECT p FROM Paiement p WHERE (:startDate IS NULL OR p.date >= :startDate) " +
            "AND (:endDate IS NULL OR p.date <= :endDate)")
    Page<Paiement> findByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    // Filter by commercant
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
            @Param("statut") String statut,
            Pageable pageable
    );
}
