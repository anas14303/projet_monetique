package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.model.HistoriqueStatutPaiement;
import com.monetique.paiement_appsb.repository.HistoriqueStatutPaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoriqueStatutPaiementService {

    @Autowired
    private HistoriqueStatutPaiementRepository historiqueStatutPaiementRepository;

    @Transactional
    public HistoriqueStatutPaiement save(HistoriqueStatutPaiement historique) {
        return historiqueStatutPaiementRepository.save(historique);
    }

    public List<HistoriqueStatutPaiement> findByPaiementId(Long paiementId) {
        return historiqueStatutPaiementRepository.findByPaiementId(paiementId);
    }

    public List<HistoriqueStatutPaiement> findAll() {
        return historiqueStatutPaiementRepository.findAll();
    }
}
