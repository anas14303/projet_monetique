package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        logger.debug("Utilisateur sauvegardé avec succès, ID: {}", savedUser.getId());
        return savedUser;
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
