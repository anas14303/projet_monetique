package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.CarteBancaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

/**
 * Repository pour les cartes bancaires
 */
@Repository
public interface CarteBancaireRepository extends JpaRepository<CarteBancaire, Long> {

    // Find by type
    @Query("SELECT cb FROM CarteBancaire cb WHERE cb.type = :type")
    Page<CarteBancaire> findByType(
            @Param("type") String type,
            Pageable pageable
    );

    Optional<CarteBancaire> findByNumeroMasked(String numeroMasked);
    
    boolean existsByNumeroMasked(String numeroMasked);
    
    boolean existsByNumeroMaskedAndIdNot(String numeroMasked, Long id);
    
    // Find by utilisateur ID
    @Query("SELECT cb FROM CarteBancaire cb WHERE cb.utilisateur.id = :utilisateurId")
    Page<CarteBancaire> findByUtilisateurId(
            @Param("utilisateurId") Long utilisateurId,
            Pageable pageable
    );

    // Search by numeroMasked and type (with optional statut)
    @Query("SELECT cb FROM CarteBancaire cb " +
           "WHERE (:numeroMasked IS NULL OR :numeroMasked = '' OR LOWER(cb.numeroMasked) LIKE LOWER(CONCAT('%', :numeroMasked, '%'))) " +
           "AND (:type IS NULL OR :type = '' OR LOWER(cb.type) LIKE LOWER(CONCAT('%', :type, '%'))) " +
           "AND (:statut IS NULL OR :statut = '' OR LOWER(cb.statut) = LOWER(:statut)) " +
           "ORDER BY " +
           "CASE WHEN :sort IS NULL OR :sort = '' THEN cb.dateCreation END DESC, " +
           "CASE WHEN :sort = 'numeroMasked' AND :direction = 'asc' THEN cb.numeroMasked END ASC, " +
           "CASE WHEN :sort = 'numeroMasked' AND :direction = 'desc' THEN cb.numeroMasked END DESC, " +
           "CASE WHEN :sort = 'type' AND :direction = 'asc' THEN cb.type END ASC, " +
           "CASE WHEN :sort = 'type' AND :direction = 'desc' THEN cb.type END DESC, " +
           "CASE WHEN :sort = 'dateExp' AND :direction = 'asc' THEN cb.dateExp END ASC, " +
           "CASE WHEN :sort = 'dateExp' AND :direction = 'desc' THEN cb.dateExp END DESC, " +
           "CASE WHEN :sort = 'dateCreation' AND :direction = 'asc' THEN cb.dateCreation END ASC, " +
           "CASE WHEN :sort = 'dateCreation' AND :direction = 'desc' THEN cb.dateCreation END DESC, " +
           "CASE WHEN :sort = 'statut' AND :direction = 'asc' THEN cb.statut END ASC, " +
           "CASE WHEN :sort = 'statut' AND :direction = 'desc' THEN cb.statut END DESC")
    Page<CarteBancaire> search(
            @Param("numeroMasked") String numeroMasked,
            @Param("type") String type,
            @Param("statut") String statut,
            @Param("sort") String sort,
            @Param("direction") String direction,
            Pageable pageable
    );
    
    // Méthode de recherche simplifiée pour la compatibilité
    default Page<CarteBancaire> findByNumeroMaskedContainingIgnoreCaseAndTypeContainingIgnoreCase(
            String numeroMasked, String type, Pageable pageable) {
        return search(
            numeroMasked, 
            type, 
            null, 
            pageable.getSort().toString().contains("numeroMasked") ? "numeroMasked" : 
                pageable.getSort().toString().contains("type") ? "type" :
                pageable.getSort().toString().contains("dateExp") ? "dateExp" :
                pageable.getSort().toString().contains("dateCreation") ? "dateCreation" :
                pageable.getSort().toString().contains("statut") ? "statut" : null,
            pageable.getSort().toString().contains("asc") ? "asc" : "desc",
            pageable
        );
    }
    
    // Méthode de recherche avec statut
    default Page<CarteBancaire> findByNumeroMaskedContainingIgnoreCaseAndTypeContainingIgnoreCaseAndStatut(
            String numeroMasked, String type, String statut, Pageable pageable) {
        return search(
            numeroMasked, 
            type, 
            statut,
            pageable.getSort().toString().contains("numeroMasked") ? "numeroMasked" : 
                pageable.getSort().toString().contains("type") ? "type" :
                pageable.getSort().toString().contains("dateExp") ? "dateExp" :
                pageable.getSort().toString().contains("dateCreation") ? "dateCreation" :
                pageable.getSort().toString().contains("statut") ? "statut" : null,
            pageable.getSort().toString().contains("asc") ? "asc" : "desc",
            pageable
        );
    }

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
