package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.service.CommercantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/commercants")
public class CommercantController {

    @Autowired
    private CommercantService commercantService;

    // CRUD operations - Only admins and commercants can manage commercants
    @PostMapping
    public ResponseEntity<Commercant> create(@RequestBody Commercant commercant) {
        return ResponseEntity.ok(commercantService.create(commercant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Commercant>> read(@PathVariable Long id) {
        return ResponseEntity.ok(commercantService.read(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Commercant> update(@PathVariable Long id, @RequestBody Commercant commercant) {
        commercant.setId(id);
        return ResponseEntity.ok(commercantService.update(commercant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commercantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Search endpoints - Available to all authenticated users
    @GetMapping
    public ResponseEntity<Page<Commercant>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commercantService.findAll(page, size));
    }

    @GetMapping("/by-name/{nom}")
    public ResponseEntity<Page<Commercant>> findByNom(
            @PathVariable String nom,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commercantService.findByNom(nom, page, size));
    }

    @GetMapping("/by-ville/{ville}")
    public ResponseEntity<Page<Commercant>> findByVille(
            @PathVariable String ville,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commercantService.findByVille(ville, page, size));
    }

    @GetMapping("/by-secteur/{secteur}")
    public ResponseEntity<Page<Commercant>> findBySecteur(
            @PathVariable String secteur,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commercantService.findBySecteur(secteur, page, size));
    }

    @GetMapping("/by-ville-and-secteur")
    public ResponseEntity<Page<Commercant>> findByVilleAndSecteur(
            @RequestParam String ville,
            @RequestParam String secteur,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commercantService.findByVilleAndSecteur(ville, secteur, page, size));
    }

    @GetMapping("/by-nom-and-ville-and-secteur")
    public ResponseEntity<Page<Commercant>> findByNomAndVilleAndSecteur(
            @RequestParam String nom,
            @RequestParam String ville,
            @RequestParam String secteur,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commercantService.findByNomAndVilleAndSecteur(nom, ville, secteur, page, size));
    }


}
