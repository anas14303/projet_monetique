package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.model.Paiement;
import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.service.PaiementService;
import com.monetique.paiement_appsb.service.CommercantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Paiement> create(@RequestBody Paiement paiement) {
        return ResponseEntity.ok(paiementService.create(paiement));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> read(@PathVariable Long id) {
        return paiementService.read(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paiement> update(@PathVariable Long id, @RequestBody Paiement paiement) {
        paiement.setId(id);
        return ResponseEntity.ok(paiementService.update(paiement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paiementService.delete(id);
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
            Optional<Commercant> commercantOpt = commercantService.read(commercantId);
            if (commercantOpt.isPresent()) {
                commercant = commercantOpt.get();
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.ok(
                paiementService.filterPaiements(statut, commercant, typeCarte, start, end, page, size)
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
        Optional<Commercant> commercantOpt = commercantService.read(id);
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
        return ResponseEntity.ok(paiementService.findByStatut(statut, page, size));
    }
}
