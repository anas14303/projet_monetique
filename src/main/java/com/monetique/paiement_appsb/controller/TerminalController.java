package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.model.Terminal;
import com.monetique.paiement_appsb.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/terminals")
public class TerminalController {

    @Autowired
    private TerminalService terminalService;

    // CRUD operations
    @PostMapping
    public ResponseEntity<Terminal> create(@RequestBody Terminal terminal) {
        return ResponseEntity.ok(terminalService.create(terminal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Terminal>> read(@PathVariable Long id) {
        return ResponseEntity.ok(terminalService.read(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Terminal> update(@PathVariable Long id, @RequestBody Terminal terminal) {
        terminal.setId(id);
        return ResponseEntity.ok(terminalService.update(terminal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        terminalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Terminal>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(terminalService.findAll(page, size));
    }

    // Search endpoints
    @GetMapping("/search/modele")
    public ResponseEntity<Page<Terminal>> findByModele(
            @RequestParam String modele,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(terminalService.findByModele(modele, page, size));
    }

    @GetMapping("/search/commercant")
    public ResponseEntity<Page<Terminal>> findByCommercant(
            @RequestParam Long commercantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(terminalService.findByCommercant(commercantId, page, size));
    }

    @GetMapping("/search/statut")
    public ResponseEntity<Page<Terminal>> findByStatut(
            @RequestParam String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(terminalService.findByStatut(statut, page, size));
    }

    // Filtering endpoints
    @GetMapping("/filter")
    public ResponseEntity<Page<Terminal>> filterTerminals(
            @RequestParam(required = false) String modele,
            @RequestParam(required = false) Long commercantId,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                terminalService.filterTerminals(modele, commercantId, statut, page, size)
        );
    }
}
