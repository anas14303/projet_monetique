package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.model.Paiement;
import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.service.PaiementService;
import com.monetique.paiement_appsb.service.CommercantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.monetique.paiement_appsb.model.StatutPaiement;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private CommercantService commercantService;

    // CRUD operations
    @PostMapping
    public ResponseEntity<Paiement> create(@RequestBody @Valid Paiement paiement) {
        return ResponseEntity.ok(paiementService.save(paiement));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> findById(@PathVariable Long id) {
        return paiementService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paiement> update(@PathVariable Long id, @RequestBody @Valid Paiement paiement) {
        Optional<Paiement> existingPaiement = paiementService.findById(id);
        if (!existingPaiement.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        paiement.setId(id);
        return ResponseEntity.ok(paiementService.save(paiement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Paiement> paiement = paiementService.findById(id);
        if (!paiement.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        paiementService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Filtering endpoints
    @GetMapping
    public ResponseEntity<Page<Paiement>> filterPaiements(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long commercantId,
            @RequestParam(required = false) String typeCarte,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = startDate != null ? dateFormat.parse(startDate) : null;
        Date end = endDate != null ? dateFormat.parse(endDate) : null;

        Commercant commercant = null;
        if (commercantId != null) {
            Optional<Commercant> commercantOptional = commercantService.findById(commercantId);
            if (commercantOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            commercant = commercantOptional.get();
        }
        
        StatutPaiement statutEnum = null;
        if (statut != null && !statut.isEmpty()) {
            try {
                statutEnum = StatutPaiement.valueOf(statut.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(
                paiementService.filterPaiements(statutEnum, commercant, typeCarte, start, end, page, size)
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<Paiement>> findByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = startDate != null ? dateFormat.parse(startDate) : null;
        Date end = endDate != null ? dateFormat.parse(endDate) : null;
        return ResponseEntity.ok(paiementService.findByDateRange(start, end, page, size));
    }

    @GetMapping("/commercant/{id}")
    public ResponseEntity<Page<Paiement>> findByCommercant(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Optional<Commercant> commercantOpt = commercantService.findById(id);
        if (commercantOpt.isPresent()) {
            return ResponseEntity.ok(paiementService.findByCommercant(commercantOpt.get(), page, size));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/type-carte")
    public ResponseEntity<Page<Paiement>> findByTypeCarte(
            @RequestParam(required = false) String typeCarte,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(paiementService.findByTypeCarte(typeCarte, page, size));
    }

    @GetMapping("/statut")
    public ResponseEntity<Page<Paiement>> findByStatut(
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (statut == null || statut.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            StatutPaiement statutEnum = StatutPaiement.valueOf(statut.toUpperCase());
            return ResponseEntity.ok(paiementService.findByStatut(statutEnum, page, size));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
