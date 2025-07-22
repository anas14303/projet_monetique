package com.monetique.paiement_appsb.controller;

import com.monetique.paiement_appsb.dto.CarteBancaireDTO;
import com.monetique.paiement_appsb.exception.ResourceNotFoundException;
import com.monetique.paiement_appsb.service.CarteBancaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/cartes-bancaires")
public class CarteBancaireController {

    @Autowired
    private CarteBancaireService carteBancaireService;

    // CRUD operations
    @PostMapping
    public ResponseEntity<CarteBancaireDTO> create(@Valid @RequestBody CarteBancaireDTO carteBancaireDTO) {
        CarteBancaireDTO savedCarte = carteBancaireService.save(carteBancaireDTO);
        return ResponseEntity.ok(savedCarte);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarteBancaireDTO> getById(@PathVariable Long id) {
        return carteBancaireService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("CarteBancaire", "id", id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarteBancaireDTO> update(
            @PathVariable Long id, 
            @Valid @RequestBody CarteBancaireDTO carteBancaireDTO) {
        CarteBancaireDTO updatedCarte = carteBancaireService.update(id, carteBancaireDTO);
        return ResponseEntity.ok(updatedCarte);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carteBancaireService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Filtering endpoints
    @GetMapping
    public ResponseEntity<Page<CarteBancaireDTO>> filter(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = startDate != null ? dateFormat.parse(startDate) : null;
        Date end = endDate != null ? dateFormat.parse(endDate) : null;

        // Gestion du tri
        Sort sortObj = Sort.by(sort[0]);
        if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
            sortObj = sortObj.descending();
        } else {
            sortObj = sortObj.ascending();
        }
        
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<CarteBancaireDTO> result;
        if (start != null || end != null || (type != null && !type.isEmpty())) {
            result = carteBancaireService.filterCartesBancaires(start, end, type, pageable);
        } else {
            result = carteBancaireService.findAll(pageable);
        }
        
        return ResponseEntity.ok(result);
    }
}
