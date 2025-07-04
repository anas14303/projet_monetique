package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.model.Paiement;
import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    // CRUD operations
    public Paiement create(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public Optional<Paiement> read(Long id) {
        return paiementRepository.findById(id);
    }

    public Paiement update(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public void delete(Long id) {
        paiementRepository.deleteById(id);
    }

    // Filtering methods
    public Page<Paiement> filterPaiements(String statut, Commercant commercant, String typeCarte,
                                          Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.filterPaiements(statut, commercant, typeCarte, startDate, endDate, pageable);
    }

    public Page<Paiement> findByDateRange(Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.findByDateRange(startDate, endDate, pageable);
    }

    public Page<Paiement> findByCommercant(Commercant commercant, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.findByCommercant(commercant, pageable);
    }

    public Page<Paiement> findByTypeCarte(String typeCarte, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.findByTypeCarte(typeCarte, pageable);
    }

    public Page<Paiement> findByStatut(String statut, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return paiementRepository.findByStatut(statut, pageable);
    }
}
