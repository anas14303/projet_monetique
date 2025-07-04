package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.CarteBancaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CarteBancaireRepository extends JpaRepository<CarteBancaire, Long> {

    // Find by type
    @Query("SELECT cb FROM CarteBancaire cb WHERE cb.type = :type")
    Page<CarteBancaire> findByType(
            @Param("type") String type,
            Pageable pageable
    );

    // Find by expiration date range
    @Query("SELECT cb FROM CarteBancaire cb " +
            "WHERE (:startDate IS NULL OR cb.dateExp >= :startDate) " +
            "AND (:endDate IS NULL OR cb.dateExp <= :endDate)")
    Page<CarteBancaire> findByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    // Combined filter
    @Query("SELECT cb FROM CarteBancaire cb " +
            "WHERE (:startDate IS NULL OR cb.dateExp >= :startDate) " +
            "AND (:endDate IS NULL OR cb.dateExp <= :endDate) " +
            "AND (:type IS NULL OR cb.type = :type)")
    Page<CarteBancaire> filterCartesBancaires(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("type") String type,
            Pageable pageable
    );
}
