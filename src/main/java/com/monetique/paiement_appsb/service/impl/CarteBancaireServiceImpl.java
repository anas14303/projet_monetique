package com.monetique.paiement_appsb.service.impl;

import com.monetique.paiement_appsb.dto.CarteBancaireDTO;
import com.monetique.paiement_appsb.exception.ResourceNotFoundException;
import com.monetique.paiement_appsb.mapper.CarteBancaireMapper;
import com.monetique.paiement_appsb.model.CarteBancaire;
import com.monetique.paiement_appsb.repository.CarteBancaireRepository;
import com.monetique.paiement_appsb.service.CarteBancaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarteBancaireServiceImpl implements CarteBancaireService {

    private final CarteBancaireRepository carteBancaireRepository;
    private final CarteBancaireMapper carteBancaireMapper;

    @Autowired
    public CarteBancaireServiceImpl(CarteBancaireRepository carteBancaireRepository,
                                   CarteBancaireMapper carteBancaireMapper) {
        this.carteBancaireRepository = carteBancaireRepository;
        this.carteBancaireMapper = carteBancaireMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarteBancaireDTO> findAll() {
        return carteBancaireRepository.findAll().stream()
                .map(carteBancaireMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarteBancaireDTO> findAll(Pageable pageable) {
        return carteBancaireRepository.findAll(pageable)
                .map(carteBancaireMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarteBancaireDTO> findById(Long id) {
        return carteBancaireRepository.findById(id)
                .map(carteBancaireMapper::toDto);
    }

    @Override
    @Transactional
    public CarteBancaireDTO save(CarteBancaireDTO carteBancaireDTO) {
        CarteBancaire carteBancaire = carteBancaireMapper.toEntity(carteBancaireDTO);
        CarteBancaire savedCarte = carteBancaireRepository.save(carteBancaire);
        return carteBancaireMapper.toDto(savedCarte);
    }

    @Override
    @Transactional
    public CarteBancaireDTO update(Long id, CarteBancaireDTO carteBancaireDTO) {
        return carteBancaireRepository.findById(id)
                .map(existingCarte -> {
                    CarteBancaire carteToUpdate = carteBancaireMapper.toEntity(carteBancaireDTO);
                    carteToUpdate.setId(id);
                    CarteBancaire updatedCarte = carteBancaireRepository.save(carteToUpdate);
                    return carteBancaireMapper.toDto(updatedCarte);
                })
                .orElseThrow(() -> new ResourceNotFoundException("CarteBancaire", "id", id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CarteBancaire carteBancaire = carteBancaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarteBancaire", "id", id));
        carteBancaireRepository.delete(carteBancaire);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarteBancaireDTO> search(String numeroMasked, String type, String statut, Pageable pageable) {
        // Extraire le champ de tri et la direction du Pageable
        String sortBy = pageable.getSort().stream()
                .findFirst()
                .map(order -> order.getProperty())
                .orElse("dateCreation");
        
        String sortDir = pageable.getSort().stream()
                .findFirst()
                .map(order -> order.getDirection().toString().toLowerCase())
                .orElse("desc");
        
        // Appeler la méthode search du repository avec les paramètres de tri
        return carteBancaireRepository.search(
            numeroMasked != null ? numeroMasked : "",
            type != null ? type : "",
            statut != null ? statut : "",
            sortBy,
            sortDir,
            pageable
        ).map(carteBancaireMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarteBancaireDTO> findByUtilisateurId(Long utilisateurId, Pageable pageable) {
        return carteBancaireRepository.findByUtilisateurId(utilisateurId, pageable)
                .map(carteBancaireMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNumeroMasked(String numeroMasked) {
        return carteBancaireRepository.existsByNumeroMasked(numeroMasked);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNumeroMaskedAndIdNot(String numeroMasked, Long id) {
        return carteBancaireRepository.existsByNumeroMaskedAndIdNot(numeroMasked, id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CarteBancaireDTO> findByNumeroMasked(String numeroMasked) {
        return carteBancaireRepository.findByNumeroMasked(numeroMasked)
                .map(carteBancaireMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CarteBancaireDTO> filterCartesBancaires(
            Date startDate,
            Date endDate,
            String type,
            Pageable pageable
    ) {
        return carteBancaireRepository.filterCartesBancaires(startDate, endDate, type, pageable)
                .map(carteBancaireMapper::toDto);
    }
}
