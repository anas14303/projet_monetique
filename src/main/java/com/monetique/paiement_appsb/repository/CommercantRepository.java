package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.Commercant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.lang.NonNull;

@Repository
public interface CommercantRepository extends JpaRepository<Commercant, Long> {

    // Find by name
    Page<Commercant> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    // Find by location
    Page<Commercant> findByVilleContainingIgnoreCaseAndSecteurContainingIgnoreCase(
            String ville,
            String secteur,
            Pageable pageable
    );

    // Find by ID
    @Override
    @NonNull
    Optional<Commercant> findById(@NonNull Long id);

    // Check existence
    @Override
    boolean existsById(@NonNull Long id);

    // Find by name and location
    @Query("SELECT c FROM Commercant c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')) " +
            "AND LOWER(c.ville) LIKE LOWER(CONCAT('%', :ville, '%')) " +
            "AND LOWER(c.secteur) LIKE LOWER(CONCAT('%', :secteur, '%'))")
    Page<Commercant> findByNomAndVilleAndSecteur(
            @Param("nom") String nom,
            @Param("ville") String ville,
            @Param("secteur") String secteur,
            Pageable pageable
    );

    // Find by location and sector
    @Query("SELECT c FROM Commercant c WHERE LOWER(c.ville) LIKE LOWER(CONCAT('%', :ville, '%')) " +
            "AND LOWER(c.secteur) LIKE LOWER(CONCAT('%', :secteur, '%'))")
    Page<Commercant> findByVilleAndSecteur(
            @Param("ville") String ville,
            @Param("secteur") String secteur,
            Pageable pageable
    );

    // Find by name
    @Query("SELECT c FROM Commercant c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    Page<Commercant> findByNom(
            @Param("nom") String nom,
            Pageable pageable
    );

    // Find by location
    @Query("SELECT c FROM Commercant c WHERE LOWER(c.ville) LIKE LOWER(CONCAT('%', :ville, '%'))")
    Page<Commercant> findByVille(
            @Param("ville") String ville,
            Pageable pageable
    );

    // Find by sector
    @Query("SELECT c FROM Commercant c WHERE LOWER(c.secteur) LIKE LOWER(CONCAT('%', :secteur, '%'))")
    Page<Commercant> findBySecteur(
            @Param("secteur") String secteur,
            Pageable pageable
    );
}
