package com.monetique.paiement_appsb.controller.api;

import com.monetique.paiement_appsb.model.Paiement;
import com.monetique.paiement_appsb.model.StatutPaiement;
import com.monetique.paiement_appsb.model.Commercant;
import com.monetique.paiement_appsb.service.PaiementService;
import com.monetique.paiement_appsb.service.CommercantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(
    origins = {
        "http://localhost:3000",
        "http://localhost:8080",
        "http://127.0.0.1:3000",
        "http://127.0.0.1:8080"
    },
    allowedHeaders = {
        "Origin", 
        "Content-Type", 
        "Accept", 
        "Authorization", 
        "X-Requested-With", 
        "X-XSRF-TOKEN", 
        "X-Auth-Token"
    },
    exposedHeaders = {
        "Authorization", 
        "X-XSRF-TOKEN", 
        "X-Auth-Token"
    },
    allowCredentials = "true",
    maxAge = 3600,
    methods = {
        RequestMethod.GET, 
        RequestMethod.POST, 
        RequestMethod.PUT, 
        RequestMethod.PATCH,
        RequestMethod.DELETE, 
        RequestMethod.OPTIONS,
        RequestMethod.HEAD
    }
)
public class TransactionApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionApiController.class);

    private final PaiementService paiementService;
    private final CommercantService commercantService;

    @Autowired
    public TransactionApiController(PaiementService paiementService, CommercantService commercantService) {
        this.paiementService = paiementService;
        this.commercantService = commercantService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTransactions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long commercantId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        
        logger.info("Début de la récupération des transactions avec filtres");
        
        try {
            // Convertir les paramètres de date
            Date start = null;
            Date end = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            if (startDate != null && !startDate.isEmpty()) {
                start = dateFormat.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = dateFormat.parse(endDate);
                // Ajouter un jour pour inclure la date de fin
                end = new Date(end.getTime() + (1000 * 60 * 60 * 24 - 1));
            }
            
            // Convertir le statut en enum
            StatutPaiement statutEnum = null;
            if (statut != null && !statut.isEmpty()) {
                try {
                    statutEnum = StatutPaiement.valueOf(statut.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Statut invalide: {}", statut);
                    return ResponseEntity.badRequest().build();
                }
            }
            
            // Récupérer les transactions filtrées
            Page<Paiement> page = paiementService.filterPaiements(statutEnum, null, null, start, end, pageable.getPageNumber(), pageable.getPageSize());
            
            // Préparer la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("data", page.getContent());
            response.put("draw", pageable.getPageNumber());
            response.put("recordsTotal", page.getTotalElements());
            response.put("recordsFiltered", page.getTotalElements());
            
            logger.info("Récupération réussie de {} transactions", page.getTotalElements());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> getTransactionById(@PathVariable Long id) {
        logger.info("Recherche de la transaction avec l'ID: {}", id);
        try {
            Optional<Paiement> transaction = paiementService.findById(id);
            if (transaction.isPresent()) {
                logger.debug("Transaction trouvée avec l'ID: {}", id);
                return ResponseEntity.ok(transaction.get());
            }
            logger.warn("Transaction non trouvée avec l'ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de la transaction avec l'ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Paiement> createTransaction(@RequestBody @Valid Paiement transaction) {
        logger.info("Tentative de création d'une nouvelle transaction");
        
        if (transaction.getId() != null) {
            logger.warn("Tentative de création d'une transaction avec un ID déjà défini: {}", transaction.getId());
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Paiement createdTransaction = paiementService.save(transaction);
            logger.info("Transaction créée avec succès avec l'ID: {}", createdTransaction.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paiement> updateTransaction(
            @PathVariable Long id,
            @RequestBody @Valid Paiement transactionDetails) {
        logger.info("Tentative de mise à jour de la transaction avec l'ID: {}", id);
        
        try {
            Optional<Paiement> existingTransaction = paiementService.findById(id);
            if (!existingTransaction.isPresent()) {
                logger.warn("Aucune transaction trouvée avec l'ID: {} pour la mise à jour", id);
                return ResponseEntity.notFound().build();
            }
            
            Paiement transaction = existingTransaction.get();
            // Mettre à jour les champs de la transaction
            transaction.setMontant(transactionDetails.getMontant());
            transaction.setDate(transactionDetails.getDate());
            transaction.setStatut(transactionDetails.getStatut());
            transaction.setTypeCarte(transactionDetails.getTypeCarte());
            transaction.setCommentaire(transactionDetails.getCommentaire());
            
            Paiement updatedTransaction = paiementService.save(transaction);
            logger.info("Transaction mise à jour avec succès avec l'ID: {}", id);
            return ResponseEntity.ok(updatedTransaction);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la transaction avec l'ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        logger.info("Suppression de la transaction avec l'ID: {}", id);
        try {
            Optional<Paiement> transaction = paiementService.findById(id);
            if (transaction.isPresent()) {
                Long commercantId = transaction.get().getCommercant().getId();
                Optional<Commercant> commercantOptional = commercantService.findById(commercantId);
                if (commercantOptional.isEmpty()) {
                    throw new RuntimeException("Commercant not found with ID: " + commercantId);
                }
                paiementService.deleteById(id);
                logger.info("Transaction supprimée avec succès avec l'ID: {}", id);
                return ResponseEntity.noContent().build();
            }
            logger.warn("Transaction non trouvée avec l'ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la transaction avec l'ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/update-status")
    public ResponseEntity<Paiement> updateTransactionStatus(
            @PathVariable Long id,
            @RequestParam String newStatus,
            @RequestParam(required = false) String comment) {
        logger.info("Tentative de mise à jour du statut de la transaction avec l'ID: {} vers {}", id, newStatus);
        
        try {
            Paiement updatedTransaction = paiementService.updateStatus(id, newStatus, comment);
            logger.info("Statut de la transaction mis à jour avec succès pour l'ID: {}", id);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la mise à jour du statut de la transaction avec l'ID: " + id, e);
            if (e.getMessage().contains("non trouv")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du statut de la transaction avec l'ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/statuses")
    public ResponseEntity<StatutPaiement[]> getTransactionStatuses() {
        return ResponseEntity.ok(StatutPaiement.values());
    }
}
