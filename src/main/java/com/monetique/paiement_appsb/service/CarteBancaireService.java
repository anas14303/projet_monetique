package com.monetique.paiement_appsb.service;

import com.monetique.paiement_appsb.model.CarteBancaire;
import com.monetique.paiement_appsb.repository.CarteBancaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class CarteBancaireService {

    @Autowired
    private CarteBancaireRepository carteBancaireRepository;

    // CRUD operations
    public CarteBancaire create(CarteBancaire carteBancaire) {
        return carteBancaireRepository.save(carteBancaire);
    }

    public Optional<CarteBancaire> read(Long id) {
        return carteBancaireRepository.findById(id);
    }

    public CarteBancaire update(CarteBancaire carteBancaire) {
        return carteBancaireRepository.save(carteBancaire);
    }

    public void delete(Long id) {
        carteBancaireRepository.deleteById(id);
    }

    // Filtering methods
    public Page<CarteBancaire> findByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateExp").descending());
        return carteBancaireRepository.findByType(type, pageable);
    }

    public Page<CarteBancaire> findByDateRange(Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateExp").descending());
        return carteBancaireRepository.findByDateRange(startDate, endDate, pageable);
    }

    public Page<CarteBancaire> filterCartesBancaires(
            Date startDate,
            Date endDate,
            String type,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateExp").descending());
        return carteBancaireRepository.filterCartesBancaires(startDate, endDate, type, pageable);
    }
}
