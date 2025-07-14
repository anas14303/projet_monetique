package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityNotFoundException;
import com.monetique.paiement_appsb.model.CarteBancaire;
import com.monetique.paiement_appsb.model.Paiement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {
    
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public Page<Utilisateur> findAll(Pageable pageable) {
        logger.info("Récupération de tous les utilisateurs avec pagination");
        Page<Utilisateur> utilisateurs = utilisateurRepository.findAll(pageable);
        logger.debug("Nombre d'utilisateurs récupérés: {}", utilisateurs.getTotalElements());
        return utilisateurs;
    }
    
    @Transactional(readOnly = true)
    public Page<Utilisateur> search(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return findAll(pageable);
        }
        return utilisateurRepository.search(
            "%" + keyword.toLowerCase() + "%", 
            pageable
        );
    }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> findById(Long id) {
        logger.info("Recherche de l'utilisateur avec l'ID: {}", id);
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);
        if (utilisateur.isPresent()) {
            logger.debug("Utilisateur trouvé: {}", utilisateur.get().getEmail());
        } else {
            logger.debug("Aucun utilisateur trouvé avec l'ID: {}", id);
        }
        return utilisateur;
    }

    @Transactional
    public Utilisateur save(Utilisateur utilisateur) {
        logger.info("Sauvegarde de l'utilisateur avec l'email: {}", utilisateur.getEmail());
        
        if (utilisateur.getId() == null && utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            logger.error("Tentative de création d'un utilisateur avec un email déjà existant: {}", utilisateur.getEmail());
            throw new IllegalStateException("Email already in use");
        }
        
        // Si c'est une mise à jour d'un utilisateur existant
        if (utilisateur.getId() != null) {
            // Charger l'utilisateur existant avec ses relations
            Utilisateur existingUser = utilisateurRepository.findById(utilisateur.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
            
            // Copier les propriétés de l'utilisateur existant
            BeanUtils.copyProperties(utilisateur, existingUser, 
                "id", "dateCreation", "lastLogin", "password", "cartesBancaires", "paiements");
            
            // Mettre à jour la collection cartesBancaires si nécessaire
            if (utilisateur.getCartesBancaires() != null) {
                // Créer une nouvelle liste pour éviter de modifier la collection existante directement
                List<CarteBancaire> cartesMisesAJour = new ArrayList<>(utilisateur.getCartesBancaires());
                
                // Mettre à jour la collection existante sans la remplacer
                existingUser.getCartesBancaires().clear();
                existingUser.getCartesBancaires().addAll(cartesMisesAJour);
                
                // Mettre à jour la référence de l'utilisateur pour chaque carte
                cartesMisesAJour.forEach(carte -> carte.setUtilisateur(existingUser));
            } else {
                // Si aucune carte n'est fournie, vider la collection existante
                existingUser.getCartesBancaires().clear();
            }
            
            // Mettre à jour la collection paiements si nécessaire
            if (utilisateur.getPaiements() != null) {
                // Créer une nouvelle liste pour éviter de modifier la collection existante directement
                List<Paiement> paiementsMisesAJour = new ArrayList<>(utilisateur.getPaiements());
                
                // Mettre à jour la collection existante sans la remplacer
                existingUser.getPaiements().clear();
                existingUser.getPaiements().addAll(paiementsMisesAJour);
                
                // La méthode updatePaiements() sera appelée automatiquement avant la persistance
            }
            
            return utilisateurRepository.save(existingUser);
        }
        
        // Pour un nouvel utilisateur, s'assurer que les références sont correctement définies
        
        // Gestion des cartes bancaires
        if (utilisateur.getCartesBancaires() != null) {
            // Créer une nouvelle liste pour éviter les problèmes de référence
            List<CarteBancaire> nouvellesCartes = new ArrayList<>(utilisateur.getCartesBancaires());
            utilisateur.setCartesBancaires(nouvellesCartes);
            
            // Mettre à jour la référence de l'utilisateur pour chaque carte
            nouvellesCartes.forEach(carte -> {
                if (carte.getUtilisateur() == null) {
                    carte.setUtilisateur(utilisateur);
                }
            });
        } else {
            utilisateur.setCartesBancaires(new ArrayList<>());
        }
        
        // Gestion des paiements
        if (utilisateur.getPaiements() != null) {
            // Créer une nouvelle liste pour éviter les problèmes de référence
            List<Paiement> nouveauxPaiements = new ArrayList<>(utilisateur.getPaiements());
            utilisateur.setPaiements(nouveauxPaiements);
            
            // La méthode updatePaiements() sera appelée automatiquement avant la persistance
        } else {
            utilisateur.setPaiements(new ArrayList<>());
        }
        
        return utilisateurRepository.save(utilisateur);
    }
    
    @Transactional(readOnly = true)
    public boolean emailExists(String email, Long excludeId) {
        if (excludeId == null) {
            return utilisateurRepository.existsByEmail(email);
        }
        return utilisateurRepository.existsByEmailAndIdNot(email, excludeId);
    }

    @Transactional
    public void deleteById(Long id) {
        utilisateurRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return utilisateurRepository.existsByEmailAndIdNot(email, id);
    }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public long count() {
        return utilisateurRepository.count();
    }
    
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return utilisateurRepository.existsById(id);
    }
}
