package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.repository.CommercantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CommercantService {

    @Autowired
    private CommercantRepository commercantRepository;

    // CRUD operations
    public Commercant create(Commercant commercant) {
        return commercantRepository.save(commercant);
    }

    public Optional<Commercant> read(Long id) {
        return commercantRepository.findById(id);
    }

    public Commercant update(Commercant commercant) {
        return commercantRepository.save(commercant);
    }

    public void delete(Long id) {
        commercantRepository.deleteById(id);
    }

    // Pagination
    public Page<Commercant> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom"));
        return commercantRepository.findAll(pageable);
    }

    // Filtering methods
    public Page<Commercant> findByNom(String nom, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return commercantRepository.findByNom(nom, pageable);
    }

    public Page<Commercant> findByVille(String ville, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return commercantRepository.findByVille(ville, pageable);
    }

    public Page<Commercant> findBySecteur(String secteur, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return commercantRepository.findBySecteur(secteur, pageable);
    }

    public Page<Commercant> findByVilleAndSecteur(String ville, String secteur, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return commercantRepository.findByVilleAndSecteur(ville, secteur, pageable);
    }

    public Page<Commercant> findByNomAndVilleAndSecteur(String nom, String ville, String secteur, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return commercantRepository.findByNomAndVilleAndSecteur(nom, ville, secteur, pageable);
    }


}
