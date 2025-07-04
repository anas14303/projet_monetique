package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.Terminal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {

    Page<Terminal> findByModele(String modele, Pageable pageable);

    Page<Terminal> findByCommercantId(Long commercantId, Pageable pageable);

    Page<Terminal> findByStatut(String statut, Pageable pageable);

    @Query("SELECT t FROM Terminal t WHERE (:modele IS NULL OR t.modele = :modele) AND " +
            "(:commercantId IS NULL OR t.commercant.id = :commercantId) AND " +
            "(:statut IS NULL OR t.statut = :statut)")
    Page<Terminal> findByModeleAndCommercantIdAndStatut(
            @Param("modele") String modele,
            @Param("commercantId") Long commercantId,
            @Param("statut") String statut,
            Pageable pageable
    );
}
