package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.Commercant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Repository pour les commerçants
 */
@Repository
public interface CommercantRepository extends JpaRepository<Commercant, Long> {

    // Find by name
    Optional<Commercant> findByNom(String nom);
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
    Page<Commercant> findByNomAndVilleAndSecteur(
            String nom,
            String ville,
            String secteur,
            Pageable pageable
    );

    // Find by location and sector
    Page<Commercant> findByVilleAndSecteur(
            String ville,
            String secteur,
            Pageable pageable
    );

    // Find by name and location
    Page<Commercant> findByNomContainingIgnoreCaseOrVilleContainingIgnoreCaseOrSecteurContainingIgnoreCase(
            String nom,
            String ville,
            String secteur,
            Pageable pageable
    );

    // Find by location
    Page<Commercant> findByVilleContainingIgnoreCase(String ville, Pageable pageable);
    Page<Commercant> findBySecteurContainingIgnoreCase(String secteur, Pageable pageable);

    // Find by name
    Page<Commercant> findByNom(String nom, Pageable pageable);
}
