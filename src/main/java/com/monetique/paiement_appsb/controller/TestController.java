package com.monetique.paiement_appsb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur de test pour vérifier le bon fonctionnement de l'application
 * et des différents niveaux d'accès.
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * Endpoint de statut de l'application
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Paiement Application");
        response.put("version", "1.0.0");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint public accessible sans authentification
     */
    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ce point de terminaison est accessible sans authentification");
        response.put("access", "public");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint protégé nécessitant une authentification
     */
    @GetMapping("/protected")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> protectedEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ce point de terminaison nécessite une authentification");
        response.put("access", "authenticated");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint administratif nécessitant le rôle ADMIN
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ce point de terminaison est réservé aux administrateurs");
        response.put("access", "admin");
        return ResponseEntity.ok(response);
    }
}
