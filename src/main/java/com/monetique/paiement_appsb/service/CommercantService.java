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

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommercantService {

    @Autowired
    private CommercantRepository commercantRepository;

    // CRUD operations
    @Transactional
    public Commercant save(Commercant commercant) {
        return commercantRepository.save(commercant);
    }

    public Page<Commercant> findAll(Pageable pageable) {
        return commercantRepository.findAll(pageable);
    }

    public Optional<Commercant> findById(Long id) {
        return commercantRepository.findById(id);
    }

    public Optional<Commercant> findByNom(String nom) {
        return commercantRepository.findByNom(nom);
    }

    public List<Commercant> findByVille(String ville) {
        return commercantRepository.findByVilleContainingIgnoreCase(ville, Pageable.unpaged()).toList();
    }

    public List<Commercant> findBySecteur(String secteur) {
        return commercantRepository.findBySecteurContainingIgnoreCase(secteur, Pageable.unpaged()).toList();
    }

    public Page<Commercant> findByNomContainingIgnoreCase(String nom, Pageable pageable) {
        return commercantRepository.findByNomContainingIgnoreCase(nom, pageable);
    }

    public Page<Commercant> findByVilleContainingIgnoreCase(String ville, Pageable pageable) {
        return commercantRepository.findByVilleContainingIgnoreCase(ville, pageable);
    }

    public Page<Commercant> findBySecteurContainingIgnoreCase(String secteur, Pageable pageable) {
        return commercantRepository.findBySecteurContainingIgnoreCase(secteur, pageable);
    }

    public Page<Commercant> findByNomContainingIgnoreCaseOrVilleContainingIgnoreCaseOrSecteurContainingIgnoreCase(String nom, String ville, String secteur, Pageable pageable) {
        return commercantRepository.findByNomContainingIgnoreCaseOrVilleContainingIgnoreCaseOrSecteurContainingIgnoreCase(nom, ville, secteur, pageable);
    }

    @Transactional
    public void deleteById(Long id) {
        commercantRepository.deleteById(id);
    }

    // Récupérer les commerçants avec pagination
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
        return commercantRepository.findByVilleContainingIgnoreCase(ville, pageable);
    }

    public Page<Commercant> findBySecteur(String secteur, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return commercantRepository.findBySecteurContainingIgnoreCase(secteur, pageable);
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
