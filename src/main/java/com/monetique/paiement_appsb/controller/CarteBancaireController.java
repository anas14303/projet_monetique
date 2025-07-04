package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.model.CarteBancaire;
import com.monetique.paiement_appsb.service.CarteBancaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/cartes-bancaires")
public class CarteBancaireController {

    @Autowired
    private CarteBancaireService carteBancaireService;

    // CRUD operations
    @PostMapping
    public CarteBancaire create(@RequestBody CarteBancaire carteBancaire) {
        return carteBancaireService.create(carteBancaire);
    }

    @GetMapping("/{id}")
    public CarteBancaire read(@PathVariable Long id) {
        return carteBancaireService.read(id)
                .orElseThrow(() -> new RuntimeException("Carte bancaire not found"));
    }

    @PutMapping("/{id}")
    public CarteBancaire update(@PathVariable Long id, @RequestBody CarteBancaire carteBancaire) {
        carteBancaire.setId(id);
        return carteBancaireService.update(carteBancaire);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carteBancaireService.delete(id);
    }

    // Filtering endpoints
    @GetMapping
    public Page<CarteBancaire> filter(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = startDate != null ? dateFormat.parse(startDate) : null;
        Date end = endDate != null ? dateFormat.parse(endDate) : null;

        return carteBancaireService.filterCartesBancaires(start, end, type, page, size);
    }

    @GetMapping("/by-date")
    public Page<CarteBancaire> findByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = startDate != null ? dateFormat.parse(startDate) : null;
        Date end = endDate != null ? dateFormat.parse(endDate) : null;
        return carteBancaireService.findByDateRange(start, end, page, size);
    }

    @GetMapping("/by-type/{type}")
    public Page<CarteBancaire> findByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return carteBancaireService.findByType(type, page, size);
    }
}
