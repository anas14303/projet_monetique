package com.monetique.paiement_appsb.repository;

import com.monetique.paiement_appsb.model.HistoriqueStatutPaiement;
import com.monetique.paiement_appsb.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueStatutPaiementRepository extends JpaRepository<HistoriqueStatutPaiement, Long> {
    List<HistoriqueStatutPaiement> findByPaiementOrderByDateModificationDesc(Paiement paiement);
    List<HistoriqueStatutPaiement> findByPaiementId(Long paiementId);
}
