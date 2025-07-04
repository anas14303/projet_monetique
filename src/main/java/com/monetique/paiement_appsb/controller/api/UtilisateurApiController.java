package com.monetique.paiement_appsb.controller.api;

import com.monetique.paiement_appsb.model.Utilisateur;
import com.monetique.paiement_appsb.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(
    origins = {
        "http://localhost:8080", 
        "http://localhost:3000",
        "http://127.0.0.1:8080",
        "http://127.0.0.1:3000"
    }, 
    allowedHeaders = {
        "origin",
        "content-type",
        "accept",
        "authorization",
        "x-requested-with",
        "x-xsrf-token",
        "x-auth-token"
    },
    exposedHeaders = {
        "x-auth-token",
        "x-xsrf-token"
    },
    allowCredentials = "true",
    maxAge = 3600,
    methods = {
        RequestMethod.GET, 
        RequestMethod.POST, 
        RequestMethod.PUT, 
        RequestMethod.DELETE, 
        RequestMethod.OPTIONS,
        RequestMethod.HEAD,
        RequestMethod.PATCH
    }
)
public class UtilisateurApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurApiController.class);

    private final UtilisateurService utilisateurService;

    @Autowired
    public UtilisateurApiController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        logger.info("Début de la récupération de tous les utilisateurs");
        try {
            Page<Utilisateur> page = utilisateurService.findAll(Pageable.unpaged());
            logger.info("Récupération réussie de {} utilisateurs", page.getTotalElements());
            return ResponseEntity.ok(page.getContent());
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        logger.info("Recherche de l'utilisateur avec l'ID: {}", id);
        try {
            return utilisateurService.findById(id)
                    .map(utilisateur -> {
                        logger.debug("Utilisateur trouvé: {}", utilisateur.getEmail());
                        return ResponseEntity.ok(utilisateur);
                    })
                    .orElseGet(() -> {
                        logger.warn("Aucun utilisateur trouvé avec l'ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de l'utilisateur avec l'ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Utilisateur> createUtilisateur(@RequestBody Utilisateur utilisateur) {
        logger.info("Tentative de création d'un nouvel utilisateur avec l'email: {}", utilisateur.getEmail());
        
        if (utilisateur.getId() != null) {
            logger.warn("Tentative de création d'un utilisateur avec un ID déjà défini: {}", utilisateur.getId());
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Utilisateur savedUser = utilisateurService.save(utilisateur);
            logger.info("Utilisateur créé avec succès, ID: {}", savedUser.getId());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(
            @PathVariable Long id, 
            @RequestBody Utilisateur utilisateur) {
        
        logger.info("Tentative de mise à jour de l'utilisateur avec l'ID: {}", id);
        
        if (utilisateur.getId() == null || !utilisateur.getId().equals(id)) {
            logger.warn("ID de l'utilisateur non valide ou incohérent. ID dans le chemin: {}, ID dans le corps: {}", 
                      id, utilisateur.getId());
            return ResponseEntity.badRequest().build();
        }
        
        if (!utilisateurService.existsById(id)) {
            logger.warn("Aucun utilisateur trouvé avec l'ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        try {
            Utilisateur updatedUser = utilisateurService.save(utilisateur);
            logger.info("Utilisateur mis à jour avec succès, ID: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur avec l'ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        logger.info("Tentative de suppression de l'utilisateur avec l'ID: {}", id);
        
        if (!utilisateurService.existsById(id)) {
            logger.warn("Tentative de suppression d'un utilisateur inexistant, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        try {
            utilisateurService.deleteById(id);
            logger.info("Utilisateur supprimé avec succès, ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'utilisateur avec l'ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
