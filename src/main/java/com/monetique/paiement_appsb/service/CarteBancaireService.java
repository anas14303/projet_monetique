package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.dto.CarteBancaireDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CarteBancaireService {
    List<CarteBancaireDTO> findAll();
    
    Page<CarteBancaireDTO> findAll(Pageable pageable);
    
    Optional<CarteBancaireDTO> findById(Long id);
    
    CarteBancaireDTO save(CarteBancaireDTO carteBancaireDTO);
    
    CarteBancaireDTO update(Long id, CarteBancaireDTO carteBancaireDTO);
    
    void delete(Long id);
    
    /**
     * Recherche des cartes bancaires avec filtres
     * @param numeroMasked Numéro masqué de la carte (peut être null)
     * @param type Type de la carte (peut être null)
     * @param statut Statut de la carte (peut être null)
     * @param pageable Informations de pagination et tri
     * @return Page de CarteBancaireDTO correspondant aux critères
     */
    Page<CarteBancaireDTO> search(String numeroMasked, String type, String statut, Pageable pageable);
    
    Page<CarteBancaireDTO> findByUtilisateurId(Long utilisateurId, Pageable pageable);
    
    boolean existsByNumeroMasked(String numeroMasked);
    
    boolean existsByNumeroMaskedAndIdNot(String numeroMasked, Long id);
    
    Optional<CarteBancaireDTO> findByNumeroMasked(String numeroMasked);
    
    /**
     * Filtre avancé des cartes bancaires
     * @param startDate Date de début (peut être null)
     * @param endDate Date de fin (peut être null)
     * @param type Type de la carte (peut être null)
     * @param pageable Informations de pagination et tri
     * @return Page de CarteBancaireDTO correspondant aux critères
     */
    Page<CarteBancaireDTO> filterCartesBancaires(
            Date startDate,
            Date endDate,
            String type,
            Pageable pageable
    );
}
